package co.com.srdejo.plazoleta.application.dto.response;

public record OrderItemResponseDto(
        Long id,
        Long dishId,
        Integer quantity
) {
}
