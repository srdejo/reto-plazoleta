package co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter;

import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;
import co.com.srdejo.plazoleta.domain.spi.IOrderPersistencePort;
import co.com.srdejo.plazoleta.infrastructure.exception.NoDataFoundException;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.OrderEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    @Override
    public PageResult<OrderModel> getAllOrders(OrderStatus orderStatus, PageRequest pageRequest, Long restaurantId) {
        Sort sort = Sort.by(pageRequest.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, "orderDate");
        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size(), sort);

        Page<OrderEntity> entityPage = orderStatus == null
                ? orderRepository.findAllByRestaurantId(restaurantId, pageable)
                : orderRepository.findAllByRestaurantIdAndStatus(restaurantId, orderStatus, pageable);
        if (entityPage.isEmpty()) {
            throw new NoDataFoundException();
        }

        return new PageResult<>(
                orderEntityMapper.toOrderModelList(entityPage.getContent()),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages());
    }

}
