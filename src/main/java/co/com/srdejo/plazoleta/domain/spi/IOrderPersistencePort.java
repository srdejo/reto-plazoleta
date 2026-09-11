package co.com.srdejo.plazoleta.domain.spi;

import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;

import java.util.List;

public interface IOrderPersistencePort {

    OrderModel saveOrder(OrderModel orderModel);
    boolean hasOrder(Long customerId, List<OrderStatus> orderStatuses);

    PageResult<OrderModel> getAllOrders(OrderStatus orderStatus, PageRequest pageRequest, Long restaurantId);

    OrderModel getOrder(Long orderId);
}
