package co.com.srdejo.plazoleta.application.dto.response;

import co.com.srdejo.plazoleta.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record FullOrderResponseDto(
        Long id,
        Long customerId,
        Long restaurantId,
        Long chefId,
        LocalDateTime orderDate,
        OrderStatus status,
        List<OrderItemResponseDto> items
) {
}
