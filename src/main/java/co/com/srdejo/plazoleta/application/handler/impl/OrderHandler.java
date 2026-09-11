package co.com.srdejo.plazoleta.application.handler.impl;

import co.com.srdejo.plazoleta.application.dto.request.OrderRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.FullOrderResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.OrderResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.PageResponseDto;
import co.com.srdejo.plazoleta.application.handler.IOrderHandler;
import co.com.srdejo.plazoleta.application.mapper.IOrderRequestMapper;
import co.com.srdejo.plazoleta.application.mapper.IOrderResponseMapper;
import co.com.srdejo.plazoleta.domain.api.IOrderServicePort;
import co.com.srdejo.plazoleta.domain.model.*;
import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;
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

    @Override
    public PageResponseDto<FullOrderResponseDto> getAllOrders(int page, int size, OrderStatus orderStatus, boolean ascending) {
        PageRequest pageRequest = new PageRequest(page, size, ascending);
        PageResult<OrderModel> pageResult = orderServicePort.getAllOrders(orderStatus, pageRequest);
        return new PageResponseDto<>(
                orderResponseMapper.toResponseList(pageResult.content()),
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages());
    }

    @Override
    public void takeOrder(Long orderId) {
        orderServicePort.takeOrder(orderId);
    }
}
