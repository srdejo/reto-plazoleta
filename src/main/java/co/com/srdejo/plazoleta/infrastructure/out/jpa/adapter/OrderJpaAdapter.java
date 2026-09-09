package co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter;

import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.spi.IOrderPersistencePort;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.OrderEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public OrderModel saveOrder(OrderModel orderModel) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(orderModel);
        orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));
        OrderEntity orderEntitySaved = orderRepository.save(orderEntity);
        return orderEntityMapper.toOrderModel(orderEntitySaved);
    }

    @Override
    public boolean hasOrder(Long customerId, List<OrderStatus> orderStatuses) {
        return orderRepository.existsByCustomerIdAndStatusIn(customerId, orderStatuses);
    }


}
