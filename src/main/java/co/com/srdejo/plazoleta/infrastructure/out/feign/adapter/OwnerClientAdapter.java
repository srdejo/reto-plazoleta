package co.com.srdejo.plazoleta.infrastructure.out.feign.adapter;

import co.com.srdejo.plazoleta.domain.spi.IOwnerClientPort;
import co.com.srdejo.plazoleta.infrastructure.exception.ServiceUnavailableException;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.OwnerClient;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OwnerClientAdapter implements IOwnerClientPort {

    private final OwnerClient ownerClient;

    @Override
    public boolean existsOwner(Long id) {
        try {
            return ownerClient.getOwnerById(id).getStatusCode().is2xxSuccessful();
        } catch (feign.FeignException.NotFound ex) {
            return false;
        } catch (feign.RetryableException ex) {
            throw new ServiceUnavailableException("owners-service", ex);
        }
    }
}
