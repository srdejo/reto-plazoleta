package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.model.PageRequestModel;
import co.com.srdejo.plazoleta.domain.model.PageResultModel;

public interface IOrderServicePort {

    OrderModel saveOrder(OrderModel orderModel);

    PageResultModel<OrderModel> getAllOrders(OrderStatus orderStatus, PageRequestModel pageRequestModel);
}
