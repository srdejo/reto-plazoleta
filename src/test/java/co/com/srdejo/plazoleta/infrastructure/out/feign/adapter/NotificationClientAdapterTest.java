package co.com.srdejo.plazoleta.infrastructure.out.feign.adapter;

import co.com.srdejo.plazoleta.infrastructure.out.feign.client.NotificationClient;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.UserClient;
import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.SendSmsRequestDto;
import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.UserResponseDto;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationClientAdapterTest {

    private static final Long CUSTOMER_ID = 20L;
    private static final Long ORDER_ID = 100L;
    private static final String PIN = "1234";

    @Mock
    private UserClient userClient;

    @Mock
    private NotificationClient notificationClient;

    private NotificationClientAdapter notificationClientAdapter;

    @BeforeEach
    void setUp() {
        notificationClientAdapter = new NotificationClientAdapter(userClient, notificationClient);
    }

    private Request feignRequest() {
        return Request.create(Request.HttpMethod.GET, "/users/20", Collections.emptyMap(),
                null, new RequestTemplate());
    }

    @Test
    void notifyOrderReady_customerFound_sendsSmsWithCustomerPhone() {
        UserResponseDto customer = new UserResponseDto(CUSTOMER_ID, "Juan", "Perez", "123", "3001234567", null, "juan@mail.com", "CUSTOMER");
        when(userClient.getUserById(CUSTOMER_ID)).thenReturn(customer);

        notificationClientAdapter.notifyOrderReady(CUSTOMER_ID, ORDER_ID, PIN);
        verify(notificationClient).sendSms(new SendSmsRequestDto("3001234567", "Tu pedido #" + ORDER_ID + " está listo para reclamar. [PIN: " + PIN + "]"));
    }

    @Test
    void notifyOrderReady_customerNotFound_doesNotPropagateException() {
        when(userClient.getUserById(CUSTOMER_ID)).thenThrow(new FeignException.NotFound(
                "Not Found", feignRequest(), null, null));

        notificationClientAdapter.notifyOrderReady(CUSTOMER_ID, ORDER_ID, any());

        verify(notificationClient, never()).sendSms(any());
    }

    @Test
    void notifyOrderReady_mensajeriaUnavailable_doesNotPropagateException() {
        UserResponseDto customer = new UserResponseDto(CUSTOMER_ID, "Juan", "Perez", "123", "3001234567", null, "juan@mail.com", "CUSTOMER");
        when(userClient.getUserById(CUSTOMER_ID)).thenReturn(customer);
        doThrow(new RetryableException(
                503, "mensajeria-service", Request.HttpMethod.POST, (Long) null, feignRequest()))
                .when(notificationClient).sendSms(any());

        notificationClientAdapter.notifyOrderReady(CUSTOMER_ID, ORDER_ID, any());
    }
}
