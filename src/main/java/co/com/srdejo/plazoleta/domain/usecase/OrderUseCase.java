package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.api.IOrderServicePort;
import co.com.srdejo.plazoleta.domain.exception.ActiveOrderExistsException;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOrderException;
import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.plazoleta.domain.spi.IDishPersistencePort;
import co.com.srdejo.plazoleta.domain.spi.IOrderPersistencePort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final IDishPersistencePort dishPersistencePort;

    public OrderUseCase(
            IOrderPersistencePort orderPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort,
            IDishPersistencePort dishPersistencePort
    ) {
        this.orderPersistencePort = orderPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
        this.dishPersistencePort = dishPersistencePort;
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
