package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.api.IOrderServicePort;
import co.com.srdejo.plazoleta.domain.exception.ActiveOrderExistsException;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOrderException;
import co.com.srdejo.plazoleta.domain.exception.UnauthorizedException;
import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.spi.*;
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
    private final IOrderTraceabilityPort orderTraceabilityPort;

    public OrderUseCase(
            IOrderPersistencePort orderPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort,
            IDishPersistencePort dishPersistencePort,
            IEmployeeClientPort employeeClientPort,
            INotificationPort notificationPort,
            IOrderTraceabilityPort orderTraceabilityPort
    ) {
        this.orderPersistencePort = orderPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
        this.dishPersistencePort = dishPersistencePort;
        this.employeeClientPort = employeeClientPort;
        this.notificationPort = notificationPort;
        this.orderTraceabilityPort = orderTraceabilityPort;
    }

    @Override
    public OrderModel saveOrder(OrderModel orderModel) {
        Long authenticatedUserId = authenticatedUserPort.getAuthenticatedUserId();
        canGetOtherOrders(authenticatedUserId);
        validateItemsBelongToSameRestaurant(orderModel);
        orderModel.setCustomerId(authenticatedUserId);
        orderModel.setOrderDate(LocalDateTime.now(ZoneId.of("America/Bogota")));
        orderModel.setStatus(OrderStatus.PENDING);
        return saveOrderTraceability(orderModel, null);
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
        validateOrderBelongsToEmployeeRestaurant(orderModel);
        OrderStatus previousStatus = orderModel.getStatus();
        orderModel.setChefId(authenticatedUserId);
        orderModel.setStatus(OrderStatus.IN_PREPARATION);
        saveOrderTraceability(orderModel, previousStatus);
    }

    @Override
    public void markOrderAsReady(Long orderId) {
        OrderModel orderModel = orderPersistencePort.getOrder(orderId);
        OrderStatus previousStatus = orderModel.getStatus();
        orderModel.markAsReady();
        saveOrderTraceability(orderModel, previousStatus);
        notificationPort.notifyOrderReady(orderModel.getCustomerId(), orderId, orderModel.getPin());
    }

    @Override
    public void markOrderAsDelivered(Long orderId, String pin) {
        OrderModel orderModel = orderPersistencePort.getOrder(orderId);
        OrderStatus previousStatus = orderModel.getStatus();
        orderModel.markAsDelivered(pin);
        saveOrderTraceability(orderModel, previousStatus);
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderModel orderModel = orderPersistencePort.getOrder(orderId);
        OrderStatus previousStatus = orderModel.getStatus();
        orderModel.cancelOrder();
        saveOrderTraceability(orderModel, previousStatus);
    }

    private void canGetOtherOrders(Long customerId) {
        if ( orderPersistencePort.hasOrder(customerId, OrderStatus.ACTIVE.stream().toList()) ) {
            throw new ActiveOrderExistsException(ErrorCodesEnum.ACTIVE_ORDER_EXISTS);
        }
    }


    private void validateOrderBelongsToEmployeeRestaurant(OrderModel orderModel) {
        Long employeeRestaurantId = employeeClientPort.getAuthenticatedEmployeeRestaurantId();
        if (!orderModel.getRestaurantId().equals(employeeRestaurantId)) {
            throw new UnauthorizedException(ErrorCodesEnum.ORDER_DIFFERENT_RESTAURANT);
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

    private OrderModel saveOrderTraceability(
            OrderModel orderModel,
            OrderStatus previousStatus
    ) {
        OrderModel savedOrderModel = orderPersistencePort.saveOrder(orderModel);

        if (previousStatus != savedOrderModel.getStatus()) {
            orderTraceabilityPort.trace(
                    savedOrderModel,
                    previousStatus
            );
        }

        return savedOrderModel;
    }
}
