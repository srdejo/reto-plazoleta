package co.com.srdejo.plazoleta.infrastructure.configuration;

import co.com.srdejo.plazoleta.domain.api.IDishCategoryServicePort;
import co.com.srdejo.plazoleta.domain.api.IDishServicePort;
import co.com.srdejo.plazoleta.domain.api.IRestaurantServicePort;
import co.com.srdejo.plazoleta.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.plazoleta.domain.spi.IDishCategoryPersistencePort;
import co.com.srdejo.plazoleta.domain.spi.IDishPersistencePort;
import co.com.srdejo.plazoleta.domain.spi.IOwnerClientPort;
import co.com.srdejo.plazoleta.domain.spi.IRestaurantPersistencePort;
import co.com.srdejo.plazoleta.domain.usecase.DishCategoryUseCase;
import co.com.srdejo.plazoleta.domain.usecase.DishUseCase;
import co.com.srdejo.plazoleta.domain.usecase.RestaurantUseCase;
import co.com.srdejo.plazoleta.infrastructure.out.feign.adapter.OwnerClientAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.OwnerClient;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter.DishCategoryJpaAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter.DishJpaAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IDishCategoryEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IDishEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IDishCategoryRepository;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IDishRepository;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import co.com.srdejo.plazoleta.infrastructure.out.security.SecurityContextUserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IRestaurantRepository restaurantRepository;
    private final IDishCategoryRepository dishCategoryRepository;
    private final IDishRepository dishRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IDishEntityMapper dishEntityMapper;
    private final IDishCategoryEntityMapper dishCategoryEntityMapper;
    private final OwnerClient ownerClient;

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IDishCategoryPersistencePort dishCategoryPersistencePort() {
        return new DishCategoryJpaAdapter(dishCategoryRepository, dishCategoryEntityMapper);
    }

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public IDishCategoryServicePort dishCategoryServicePort() {
        return new DishCategoryUseCase(dishCategoryPersistencePort());
    }

    @Bean
    public IDishServicePort dishServicePort() {
        return new DishUseCase(dishPersistencePort(), restaurantPersistencePort(), ownerClientPort(), dishCategoryPersistencePort(), authenticatedUserPort());
    }

    @Bean
    public IAuthenticatedUserPort authenticatedUserPort() {
        return new SecurityContextUserAdapter();
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