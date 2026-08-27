package co.com.srdejo.plazoleta.infrastructure.out.feign.adapter;

import co.com.srdejo.plazoleta.infrastructure.exception.ServiceUnavailableException;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.OwnerClient;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerClientAdapterTest {

    @Mock
    private OwnerClient ownerClient;

    private OwnerClientAdapter ownerClientAdapter;

    @BeforeEach
    void setUp() {
        ownerClientAdapter = new OwnerClientAdapter(ownerClient);
    }

    private Request feignRequest() {
        return Request.create(Request.HttpMethod.GET, "/owners/1", java.util.Collections.emptyMap(),
                null, new RequestTemplate());
    }

    @Test
    void existsOwner_ownerFound_returnsTrue() {
        when(ownerClient.getOwnerById(1L)).thenReturn(org.springframework.http.ResponseEntity.ok().build());

        boolean result = ownerClientAdapter.existsOwner(1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsOwner_ownerNotFound_returnsFalse() {
        when(ownerClient.getOwnerById(1L)).thenThrow(new FeignException.NotFound(
                "Not Found", feignRequest(), null, null));

        boolean result = ownerClientAdapter.existsOwner(1L);

        assertThat(result).isFalse();
    }

    @Test
    void existsOwner_serviceUnavailable_throwsServiceUnavailableException() {
        when(ownerClient.getOwnerById(1L)).thenThrow(new RetryableException(
                503, "owners-service", Request.HttpMethod.GET, (Long) null, feignRequest()));

        assertThatThrownBy(() -> ownerClientAdapter.existsOwner(1L))
                .isInstanceOf(ServiceUnavailableException.class);
    }
}
