package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.model.OrderModel;

public interface IOrderServicePort {

    OrderModel saveOrder(OrderModel orderModel);
}
