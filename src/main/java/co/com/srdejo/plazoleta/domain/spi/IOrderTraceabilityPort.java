package co.com.srdejo.plazoleta.domain.spi;

import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;

public interface IOrderTraceabilityPort {

    void trace(OrderModel orderModel, OrderStatus previousStatus);
}
