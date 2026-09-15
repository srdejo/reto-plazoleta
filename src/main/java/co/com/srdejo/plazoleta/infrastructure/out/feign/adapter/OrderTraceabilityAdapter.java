package co.com.srdejo.plazoleta.infrastructure.out.feign.adapter;

import co.com.srdejo.plazoleta.domain.model.OrderModel;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import co.com.srdejo.plazoleta.domain.spi.IOrderTraceabilityPort;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.TraceabilityClient;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.UserClient;
import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.TraceabilityRequestDto;
import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.UserResponseDto;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class OrderTraceabilityAdapter implements IOrderTraceabilityPort {

    private final TraceabilityClient traceabilityClient;
    private final UserClient userClient;

    public OrderTraceabilityAdapter(TraceabilityClient traceabilityClient, UserClient userClient) {
        this.traceabilityClient = traceabilityClient;
        this.userClient = userClient;
    }

    @Async
    @Override
    public void trace(OrderModel orderModel, OrderStatus previousStatus) {
        String customerEmail = getEmailSafely(orderModel.getCustomerId());
        String employeeEmail = getEmailSafely(orderModel.getChefId());

        TraceabilityRequestDto orderRequestDto = new TraceabilityRequestDto(
                orderModel.getId(),
                orderModel.getCustomerId(),
                customerEmail,
                previousStatus == null ? null : previousStatus.name(),
                orderModel.getStatus().name(),
                orderModel.getChefId(),
                employeeEmail
        );

        traceabilityClient.registerStatusChange(orderRequestDto);
    }

    private String getEmailSafely(Long userId) {
        if (userId == null) {
            return "";
        }
        try {
            UserResponseDto user = userClient.getUserById(userId);
            return user.email();
        } catch (FeignException ex) {
            log.error("No se pudo obtener el correo del usuario {}", userId, ex);
            return "";
        }
    }
}
