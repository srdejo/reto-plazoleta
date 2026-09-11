package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.exception.ActiveOrderExistsException;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOrderException;
import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.model.OrderItemModel;
import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.plazoleta.domain.spi.IDishPersistencePort;
import co.com.srdejo.plazoleta.domain.spi.IEmployeeClientPort;
import co.com.srdejo.plazoleta.domain.spi.IOrderPersistencePort;
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

    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp() {
        orderUseCase = new OrderUseCase(orderPersistencePort, authenticatedUserPort, dishPersistencePort, employeeClientPort);
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

        orderUseCase.takeOrder(ORDER_ID);

        verify(orderPersistencePort).getOrder(ORDER_ID);
        verifyNoInteractions(dishPersistencePort);
        verifyNoInteractions(employeeClientPort);
    }
}
