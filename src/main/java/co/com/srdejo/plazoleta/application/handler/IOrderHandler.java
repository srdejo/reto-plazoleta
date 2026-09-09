package co.com.srdejo.plazoleta.application.handler;

import co.com.srdejo.plazoleta.application.dto.request.OrderRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.OrderResponseDto;

public interface IOrderHandler {

    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);
}
