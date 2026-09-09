package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.api.IOrderServicePort;
import co.com.srdejo.plazoleta.domain.exception.ActiveOrderExistsException;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOrderException;
import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.model.PageRequestModel;
import co.com.srdejo.plazoleta.domain.model.PageResultModel;
import co.com.srdejo.plazoleta.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.plazoleta.domain.spi.IDishPersistencePort;
import co.com.srdejo.plazoleta.domain.spi.IEmployeeClientPort;
import co.com.srdejo.plazoleta.domain.spi.IOrderPersistencePort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final IDishPersistencePort dishPersistencePort;
    private final IEmployeeClientPort employeeClientPort;

    public OrderUseCase(
            IOrderPersistencePort orderPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort,
            IDishPersistencePort dishPersistencePort,
            IEmployeeClientPort employeeClientPort
    ) {
        this.orderPersistencePort = orderPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
        this.dishPersistencePort = dishPersistencePort;
        this.employeeClientPort = employeeClientPort;
    }

    @Override
    public OrderModel saveOrder(OrderModel orderModel) {
        Long authenticatedUserId = authenticatedUserPort.getAuthenticatedUserId();
        canGetOtherOrders(authenticatedUserId);
        validateItemsBelongToSameRestaurant(orderModel);
        orderModel.setCustomerId(authenticatedUserId);
        orderModel.setOrderDate(LocalDateTime.now());
        orderModel.setStatus(OrderStatus.PENDING);
        return orderPersistencePort.saveOrder(orderModel);
    }

    @Override
    public PageResultModel<OrderModel> getAllOrders(OrderStatus orderStatus, PageRequestModel pageRequestModel) {
        Long restaurantId = employeeClientPort.getAuthenticatedEmployeeRestaurantId();
        return orderPersistencePort.getAllOrders(orderStatus, pageRequestModel, restaurantId);
    }

    private void canGetOtherOrders(Long customerId) {
        List<OrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(OrderStatus.PENDING);
        orderStatuses.add(OrderStatus.IN_PREPARATION);
        orderStatuses.add(OrderStatus.READY);
        if ( orderPersistencePort.hasOrder(customerId, orderStatuses) ) {
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
