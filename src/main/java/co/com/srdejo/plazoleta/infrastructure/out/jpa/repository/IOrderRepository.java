package co.com.srdejo.plazoleta.infrastructure.out.jpa.repository;

import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {

    boolean existsByCustomerIdAndStatusIn(Long customerId, List<OrderStatus> status);

    Page<OrderEntity> findAllByRestaurantIdAndStatus(Long restaurantId, OrderStatus status, Pageable pageable);

    Page<OrderEntity> findAllByRestaurantId(Long restaurantId, Pageable pageable);
}
