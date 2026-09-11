package co.com.srdejo.plazoleta.infrastructure.out.feign.adapter;

import co.com.srdejo.plazoleta.domain.spi.INotificationPort;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.NotificationClient;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.UserClient;
import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.SendSmsRequestDto;
import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.UserResponseDto;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class NotificationClientAdapter implements INotificationPort {

    private final UserClient userClient;
    private final NotificationClient notificationClient;

    @Override
    public void notifyOrderReady(Long customerId, Long orderId, String pin) {
        try {
            UserResponseDto customer = userClient.getUserById(customerId);
            String message = "Tu pedido #" + orderId + " está listo para reclamar. [PIN: " + pin + "]";
            notificationClient.sendSms(new SendSmsRequestDto(customer.phone(), message));
        } catch (FeignException ex) {
            log.error("No se pudo notificar al cliente {} sobre el pedido {}", customerId, orderId, ex);
        }
    }
}
