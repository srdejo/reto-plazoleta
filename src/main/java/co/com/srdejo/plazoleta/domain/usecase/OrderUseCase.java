package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.api.IOrderServicePort;
import co.com.srdejo.plazoleta.domain.exception.ActiveOrderExistsException;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOrderException;
import co.com.srdejo.plazoleta.domain.exception.UnauthorizedException;
import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.spi.*;
import co.com.srdejo.plazoleta.domain.utils.DomainConstants;
import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
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
        orderModel.setOrderDate(LocalDateTime.now(DomainConstants.APPLICATION_ZONE_ID));
        orderModel.setStatus(OrderStatus.PENDING);
        OrderModel savedOrder = saveOrderTraceability(orderModel, null);
        log.info("Order {} created by customer {} for restaurant {}",
                savedOrder.getId(), authenticatedUserId, orderModel.getRestaurantId());
        return savedOrder;
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
        log.info("Order {} taken by employee {} ({} -> {})", orderId, authenticatedUserId, previousStatus, orderModel.getStatus());
    }

    @Override
    public void markOrderAsReady(Long orderId) {
        OrderModel orderModel = orderPersistencePort.getOrder(orderId);
        OrderStatus previousStatus = orderModel.getStatus();
        orderModel.markAsReady();
        saveOrderTraceability(orderModel, previousStatus);
        log.info("Order {} marked as ready ({} -> {})", orderId, previousStatus, orderModel.getStatus());
        notificationPort.notifyOrderReady(orderModel.getCustomerId(), orderId, orderModel.getPin());
    }

    @Override
    public void markOrderAsDelivered(Long orderId, String pin) {
        OrderModel orderModel = orderPersistencePort.getOrder(orderId);
        OrderStatus previousStatus = orderModel.getStatus();
        orderModel.markAsDelivered(pin);
        saveOrderTraceability(orderModel, previousStatus);
        log.info("Order {} marked as delivered ({} -> {})", orderId, previousStatus, orderModel.getStatus());
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderModel orderModel = orderPersistencePort.getOrder(orderId);
        OrderStatus previousStatus = orderModel.getStatus();
        orderModel.cancelOrder();
        saveOrderTraceability(orderModel, previousStatus);
        log.info("Order {} cancelled ({} -> {})", orderId, previousStatus, orderModel.getStatus());
    }

    private void canGetOtherOrders(Long customerId) {
        if ( orderPersistencePort.hasOrder(customerId, OrderStatus.ACTIVE.stream().toList()) ) {
            log.warn("Customer {} attempted to create an order while already having an active one", customerId);
            throw new ActiveOrderExistsException(ErrorCodesEnum.ACTIVE_ORDER_EXISTS);
        }
    }


    private void validateOrderBelongsToEmployeeRestaurant(OrderModel orderModel) {
        Long employeeRestaurantId = employeeClientPort.getAuthenticatedEmployeeRestaurantId();
        if (!orderModel.getRestaurantId().equals(employeeRestaurantId)) {
            log.warn("Employee's restaurant {} does not match order {} restaurant {}",
                    employeeRestaurantId, orderModel.getId(), orderModel.getRestaurantId());
            throw new UnauthorizedException(ErrorCodesEnum.ORDER_DIFFERENT_RESTAURANT);
        }
    }

    private void validateItemsBelongToSameRestaurant(OrderModel orderModel) {
        boolean allItemsBelongToRestaurant = orderModel.getItems().stream()
                .map(item -> dishPersistencePort.findById(item.getDishId()))
                .allMatch(dish -> dish.getRestaurantId().equals(orderModel.getRestaurantId()));

        if (!allItemsBelongToRestaurant) {
            log.warn("Order for restaurant {} contains dishes from a different restaurant", orderModel.getRestaurantId());
            throw new InvalidOrderException(ErrorCodesEnum.DISHES_DIFFERENT_RESTAURANT);
        }
    }

    private OrderModel saveOrderTraceability(
            OrderModel orderModel,
            OrderStatus previousStatus
    ) {
        OrderModel savedOrderModel = orderPersistencePort.saveOrder(orderModel);

        if (previousStatus != savedOrderModel.getStatus()) {
            log.debug("Order {} status changed {} -> {}, sending traceability event",
                    savedOrderModel.getId(), previousStatus, savedOrderModel.getStatus());
            orderTraceabilityPort.trace(
                    savedOrderModel,
                    previousStatus
            );
        }

        return savedOrderModel;
    }
}
