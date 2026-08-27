package co.com.srdejo.plazoleta.infrastructure.configuration;

import co.com.srdejo.plazoleta.domain.api.IRestaurantServicePort;
import co.com.srdejo.plazoleta.domain.spi.IOwnerClientPort;
import co.com.srdejo.plazoleta.domain.spi.IRestaurantPersistencePort;
import co.com.srdejo.plazoleta.domain.usecase.RestaurantUseCase;
import co.com.srdejo.plazoleta.infrastructure.out.feign.adapter.OwnerClientAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.OwnerClient;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final OwnerClient ownerClient;

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort() {
        return new RestaurantUseCase(restaurantPersistencePort(), ownerClientPort());
    }

    @Bean
    public IOwnerClientPort  ownerClientPort() {
        return new OwnerClientAdapter(ownerClient);
    }
}