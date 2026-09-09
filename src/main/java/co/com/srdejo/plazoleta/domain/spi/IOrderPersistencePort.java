package co.com.srdejo.plazoleta.domain.spi;

import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.model.PageRequestModel;
import co.com.srdejo.plazoleta.domain.model.PageResultModel;

import java.util.List;

public interface IOrderPersistencePort {

    OrderModel saveOrder(OrderModel orderModel);
    boolean hasOrder(Long customerId, List<OrderStatus> orderStatuses);

    PageResultModel<OrderModel> getAllOrders(OrderStatus orderStatus, PageRequestModel pageRequestModel, Long restaurantId);
}
