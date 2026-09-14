package co.com.srdejo.plazoleta.application.handler;

import co.com.srdejo.plazoleta.application.dto.request.OrderRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.FullOrderResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.OrderResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.PageResponseDto;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;

public interface IOrderHandler {

    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    PageResponseDto<FullOrderResponseDto> getAllOrders(int page, int size, OrderStatus orderStatus, boolean asc);

    void takeOrder(Long orderId);

    void markOrderAsReady(Long orderId);

    void markOrderAsDelivered(Long orderId, String pin);

    void cancelOrder(Long orderId);
}
