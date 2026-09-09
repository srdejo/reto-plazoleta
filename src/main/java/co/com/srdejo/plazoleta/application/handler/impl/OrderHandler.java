package co.com.srdejo.plazoleta.application.handler.impl;

import co.com.srdejo.plazoleta.application.dto.request.OrderRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.OrderResponseDto;
import co.com.srdejo.plazoleta.application.handler.IOrderHandler;
import co.com.srdejo.plazoleta.application.mapper.IOrderRequestMapper;
import co.com.srdejo.plazoleta.application.mapper.IOrderResponseMapper;
import co.com.srdejo.plazoleta.domain.api.IOrderServicePort;
import co.com.srdejo.plazoleta.domain.model.OrderModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        OrderModel orderSaved = orderServicePort.saveOrder(orderRequestMapper.toModel(orderRequestDto));
        return orderResponseMapper.toOrderResponseDto(orderSaved);
    }
}
