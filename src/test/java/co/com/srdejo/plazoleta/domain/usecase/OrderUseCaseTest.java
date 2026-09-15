package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.exception.ActiveOrderExistsException;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOrderException;
import co.com.srdejo.plazoleta.domain.exception.InvalidOrderPinException;
import co.com.srdejo.plazoleta.domain.exception.NotYetReadyOrderException;
import co.com.srdejo.plazoleta.domain.exception.OrderCannotBeCancelledException;
import co.com.srdejo.plazoleta.domain.exception.UnauthorizedException;
import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.model.OrderItemModel;
import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.spi.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    private static final Long CUSTOMER_ID = 20L;
    private static final Long RESTAURANT_ID = 1L;
    private static final Long CHEF_ID = 30L;
    private static final Long ORDER_ID = 100L;

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IEmployeeClientPort employeeClientPort;

    @Mock
    private INotificationPort notificationPort;

    @Mock
    private IOrderTraceabilityPort orderTraceabilityPort;

    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp() {
        orderUseCase = new OrderUseCase(orderPersistencePort, authenticatedUserPort, dishPersistencePort,
                employeeClientPort, notificationPort, orderTraceabilityPort);
        lenient().when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CUSTOMER_ID);
        lenient().when(orderPersistencePort.hasOrder(any(), anyList())).thenReturn(false);
    }

    private OrderModel orderModel(List<OrderItemModel> items) {
        return new OrderModel(null, RESTAURANT_ID, null, items);
    }

    private DishModel dishModel(Long id, Long restaurantId) {
        return new DishModel(id, "Hamburguesa", null, 1L, restaurantId, "desc", "http://image.png", true);
    }

    @Test
    void saveOrder_validOrder_setsStatusPending() {
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 2)));
        when(dishPersistencePort.findById(1L)).thenReturn(dishModel(1L, RESTAURANT_ID));
        when(orderPersistencePort.saveOrder(order)).thenReturn(order);

        orderUseCase.saveOrder(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void saveOrder_validOrder_persistsOrderWithRestaurantDishesAndQuantities() {
        OrderItemModel item = new OrderItemModel(null, 1L, 3);
        OrderModel order = orderModel(List.of(item));
        when(dishPersistencePort.findById(1L)).thenReturn(dishModel(1L, RESTAURANT_ID));
        when(orderPersistencePort.saveOrder(order)).thenReturn(order);

        orderUseCase.saveOrder(order);

        assertThat(order.getRestaurantId()).isEqualTo(RESTAURANT_ID);
        assertThat(order.getItems()).containsExactly(item);
        assertThat(item.getQuantity()).isEqualTo(3);
        verify(orderPersistencePort).saveOrder(order);
    }

    @Test
    void saveOrder_validOrder_returnsPersistedOrderWithGeneratedId() {
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 2)));
        OrderModel persistedOrder = orderModel(List.of(new OrderItemModel(null, 1L, 2)));
        persistedOrder.setId(ORDER_ID);
        persistedOrder.setStatus(OrderStatus.PENDING);
        when(dishPersistencePort.findById(1L)).thenReturn(dishModel(1L, RESTAURANT_ID));
        when(orderPersistencePort.saveOrder(order)).thenReturn(persistedOrder);

        OrderModel result = orderUseCase.saveOrder(order);

        assertThat(result.getId()).isEqualTo(ORDER_ID);
    }

    @Test
    void saveOrder_validOrder_tracesPersistedOrderWithGeneratedId() {
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 2)));
        OrderModel persistedOrder = orderModel(List.of(new OrderItemModel(null, 1L, 2)));
        persistedOrder.setId(ORDER_ID);
        persistedOrder.setStatus(OrderStatus.PENDING);
        when(dishPersistencePort.findById(1L)).thenReturn(dishModel(1L, RESTAURANT_ID));
        when(orderPersistencePort.saveOrder(order)).thenReturn(persistedOrder);

        orderUseCase.saveOrder(order);

        verify(orderTraceabilityPort).trace(persistedOrder, null);
    }

    @Test
    void saveOrder_customerHasActiveOrder_throwsActiveOrderExistsException() {
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 1)));
        when(orderPersistencePort.hasOrder(CUSTOMER_ID,
                List.of(OrderStatus.PENDING, OrderStatus.IN_PREPARATION, OrderStatus.READY)))
                .thenReturn(true);

        assertThatThrownBy(() -> orderUseCase.saveOrder(order))
                .isInstanceOf(ActiveOrderExistsException.class)
                .satisfies(ex -> assertThat(((ActiveOrderExistsException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.ACTIVE_ORDER_EXISTS));

        verifyNoInteractions(dishPersistencePort);
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void saveOrder_itemsFromDifferentRestaurants_throwsInvalidOrderException() {
        Long otherRestaurantId = 2L;
        OrderModel order = orderModel(List.of(
                new OrderItemModel(null, 1L, 1),
                new OrderItemModel(null, 2L, 1)
        ));
        when(dishPersistencePort.findById(1L)).thenReturn(dishModel(1L, RESTAURANT_ID));
        when(dishPersistencePort.findById(2L)).thenReturn(dishModel(2L, otherRestaurantId));

        assertThatThrownBy(() -> orderUseCase.saveOrder(order))
                .isInstanceOf(InvalidOrderException.class)
                .satisfies(ex -> assertThat(((InvalidOrderException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.DISHES_DIFFERENT_RESTAURANT));

        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void takeOrder_validOrder_assignsChefAndSetsStatusInPreparation() {
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 1)));
        order.setStatus(OrderStatus.PENDING);
        when(orderPersistencePort.getOrder(ORDER_ID)).thenReturn(order);
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CHEF_ID);
        when(employeeClientPort.getAuthenticatedEmployeeRestaurantId()).thenReturn(RESTAURANT_ID);
        when(orderPersistencePort.saveOrder(order)).thenReturn(order);

        orderUseCase.takeOrder(ORDER_ID);

        assertThat(order.getChefId()).isEqualTo(CHEF_ID);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.IN_PREPARATION);
        verify(orderPersistencePort).saveOrder(order);
    }

    @Test
    void takeOrder_validOrder_fetchesOrderByIdBeforeSaving() {
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 1)));
        when(orderPersistencePort.getOrder(ORDER_ID)).thenReturn(order);
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CHEF_ID);
        when(employeeClientPort.getAuthenticatedEmployeeRestaurantId()).thenReturn(RESTAURANT_ID);
        when(orderPersistencePort.saveOrder(order)).thenReturn(order);

        orderUseCase.takeOrder(ORDER_ID);

        verify(orderPersistencePort).getOrder(ORDER_ID);
        verifyNoInteractions(dishPersistencePort);
    }

    @Test
    void takeOrder_orderBelongsToDifferentRestaurant_throwsUnauthorizedException() {
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 1)));
        when(orderPersistencePort.getOrder(ORDER_ID)).thenReturn(order);
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CHEF_ID);
        when(employeeClientPort.getAuthenticatedEmployeeRestaurantId()).thenReturn(RESTAURANT_ID + 1);

        assertThatThrownBy(() -> orderUseCase.takeOrder(ORDER_ID))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> assertThat(((UnauthorizedException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.ORDER_DIFFERENT_RESTAURANT));

        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void markOrderAsReady_validOrder_setsStatusReadyAndNotifiesCustomer() {
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 1)));
        order.setCustomerId(CUSTOMER_ID);
        when(orderPersistencePort.getOrder(ORDER_ID)).thenReturn(order);
        when(orderPersistencePort.saveOrder(order)).thenReturn(order);

        orderUseCase.markOrderAsReady(ORDER_ID);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.READY);
        verify(orderPersistencePort).saveOrder(order);
        verify(notificationPort).notifyOrderReady(CUSTOMER_ID, ORDER_ID, order.getPin());
    }

    @Test
    void markOrderAsDelivered_correctPinAndReadyOrder_setsStatusDelivered() {
        // given
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 1)));
        order.markAsReady();
        String correctPin = order.getPin();
        when(orderPersistencePort.getOrder(ORDER_ID)).thenReturn(order);
        when(orderPersistencePort.saveOrder(order)).thenReturn(order);

        // when
        orderUseCase.markOrderAsDelivered(ORDER_ID, correctPin);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        verify(orderPersistencePort).saveOrder(order);
    }

    @Test
    void markOrderAsDelivered_incorrectPin_throwsInvalidOrderPinException() {
        // given
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 1)));
        order.markAsReady();
        when(orderPersistencePort.getOrder(ORDER_ID)).thenReturn(order);

        // when
        assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, "wrong-pin"))
                // then
                .isInstanceOf(InvalidOrderPinException.class)
                .satisfies(ex -> assertThat(((InvalidOrderPinException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.INVALID_PIN_EXCEPTION));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.READY);
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void markOrderAsDelivered_orderNotReady_throwsNotYetReadyOrderException() {
        // given
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 1)));
        order.setStatus(OrderStatus.IN_PREPARATION);
        when(orderPersistencePort.getOrder(ORDER_ID)).thenReturn(order);

        // when
        assertThatThrownBy(() -> orderUseCase.markOrderAsDelivered(ORDER_ID, "12345"))
                // then
                .isInstanceOf(NotYetReadyOrderException.class)
                .satisfies(ex -> assertThat(((NotYetReadyOrderException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.NOT_YET_READY_EXCEPTION));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.IN_PREPARATION);
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void cancelOrder_pendingOrder_setsStatusCancelled() {
        // given
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 1)));
        order.setStatus(OrderStatus.PENDING);
        when(orderPersistencePort.getOrder(ORDER_ID)).thenReturn(order);
        when(orderPersistencePort.saveOrder(order)).thenReturn(order);

        // when
        orderUseCase.cancelOrder(ORDER_ID);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderPersistencePort).saveOrder(order);
    }

    @Test
    void cancelOrder_orderNotPending_throwsOrderCannotBeCancelledException() {
        // given
        OrderModel order = orderModel(List.of(new OrderItemModel(null, 1L, 1)));
        order.setStatus(OrderStatus.IN_PREPARATION);
        when(orderPersistencePort.getOrder(ORDER_ID)).thenReturn(order);

        // when
        assertThatThrownBy(() -> orderUseCase.cancelOrder(ORDER_ID))
                // then
                .isInstanceOf(OrderCannotBeCancelledException.class)
                .satisfies(ex -> assertThat(((OrderCannotBeCancelledException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.ORDER_CANNOT_BE_CANCELLED));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.IN_PREPARATION);
        verify(orderPersistencePort, never()).saveOrder(any());
    }
}
