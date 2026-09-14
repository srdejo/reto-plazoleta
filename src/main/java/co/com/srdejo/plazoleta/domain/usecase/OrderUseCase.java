package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.api.IOrderServicePort;
import co.com.srdejo.plazoleta.domain.exception.ActiveOrderExistsException;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOrderException;
import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.plazoleta.domain.spi.IDishPersistencePort;
import co.com.srdejo.plazoleta.domain.spi.IEmployeeClientPort;
import co.com.srdejo.plazoleta.domain.spi.INotificationPort;
import co.com.srdejo.plazoleta.domain.spi.IOrderPersistencePort;
import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final IDishPersistencePort dishPersistencePort;
    private final IEmployeeClientPort employeeClientPort;
    private final INotificationPort notificationPort;

    public OrderUseCase(
            IOrderPersistencePort orderPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort,
            IDishPersistencePort dishPersistencePort,
            IEmployeeClientPort employeeClientPort,
            INotificationPort notificationPort
    ) {
        this.orderPersistencePort = orderPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
        this.dishPersistencePort = dishPersistencePort;
        this.employeeClientPort = employeeClientPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public OrderModel saveOrder(OrderModel orderModel) {
        Long authenticatedUserId = authenticatedUserPort.getAuthenticatedUserId();
        canGetOtherOrders(authenticatedUserId);
        validateItemsBelongToSameRestaurant(orderModel);
        orderModel.setCustomerId(authenticatedUserId);
        orderModel.setOrderDate(LocalDateTime.now(ZoneId.of("America/Bogota")));
        orderModel.setStatus(OrderStatus.PENDING);
        return orderPersistencePort.saveOrder(orderModel);
    }

    @Override
    public PageResult<OrderModel> getAllOrders(OrderStatus orderStatus, PageRequest pageRequest) {
        Long restaurantId = employeeClientPort.getAuthenticatedEmployeeRestaurantId();
        return orderPersistencePort.getAllOrders(orderStatus, pageRequest, restaurantId);
    }

    @Override
    public void takeOrder(Long orderId) {
        Long authenticatedUserId = authenticatedUserPort.getAuthenticatedUserId();
        OrderModel orderModel = orderPersistencePort.getOrder(orderId);
        orderModel.setChefId(authenticatedUserId);
        orderModel.setStatus(OrderStatus.IN_PREPARATION);
        orderPersistencePort.saveOrder(orderModel);
    }

    @Override
    public void markOrderAsReady(Long orderId) {
        OrderModel orderModel = orderPersistencePort.getOrder(orderId);
        orderModel.markAsReady();
        orderPersistencePort.saveOrder(orderModel);
        notificationPort.notifyOrderReady(orderModel.getCustomerId(), orderId, orderModel.getPin());
    }

    @Override
    public void markOrderAsDelivered(Long orderId, String pin) {
        OrderModel orderModel = orderPersistencePort.getOrder(orderId);
        orderModel.markAsDelivered(pin);
        orderPersistencePort.saveOrder(orderModel);
    }

    private void canGetOtherOrders(Long customerId) {
        if ( orderPersistencePort.hasOrder(customerId, OrderStatus.ACTIVE.stream().toList()) ) {
            throw new ActiveOrderExistsException(ErrorCodesEnum.ACTIVE_ORDER_EXISTS);
        }
    }


    private void validateItemsBelongToSameRestaurant(OrderModel orderModel) {
        boolean allItemsBelongToRestaurant = orderModel.getItems().stream()
                .map(item -> dishPersistencePort.findById(item.getDishId()))
                .allMatch(dish -> dish.getRestaurantId().equals(orderModel.getRestaurantId()));

        if (!allItemsBelongToRestaurant) {
            throw new InvalidOrderException(ErrorCodesEnum.DISHES_DIFFERENT_RESTAURANT);
        }
    }
}
