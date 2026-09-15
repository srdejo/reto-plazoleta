package co.com.srdejo.plazoleta.infrastructure.out.feign.dto;

public record TraceabilityRequestDto(
        Long orderId,
        Long customerId,
        String customerEmail,
        String previousStatus,
        String newStatus,
        Long employeeId,
        String employeeEmail
) {
}
