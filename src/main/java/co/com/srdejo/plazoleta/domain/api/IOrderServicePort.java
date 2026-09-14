package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;

public interface IOrderServicePort {

    OrderModel saveOrder(OrderModel orderModel);

    PageResult<OrderModel> getAllOrders(OrderStatus orderStatus, PageRequest pageRequest);

    void takeOrder(Long orderId);

    void markOrderAsReady(Long orderId);

    void markOrderAsDelivered(Long orderId, String pin);
}
