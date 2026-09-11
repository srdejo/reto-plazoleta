package co.com.srdejo.plazoleta.infrastructure.configuration;

import co.com.srdejo.plazoleta.domain.api.IDishCategoryServicePort;
import co.com.srdejo.plazoleta.domain.api.IDishServicePort;
import co.com.srdejo.plazoleta.domain.api.IOrderServicePort;
import co.com.srdejo.plazoleta.domain.api.IRestaurantServicePort;
import co.com.srdejo.plazoleta.domain.spi.*;
import co.com.srdejo.plazoleta.domain.usecase.DishCategoryUseCase;
import co.com.srdejo.plazoleta.domain.usecase.DishUseCase;
import co.com.srdejo.plazoleta.domain.usecase.OrderUseCase;
import co.com.srdejo.plazoleta.domain.usecase.RestaurantUseCase;
import co.com.srdejo.plazoleta.infrastructure.out.feign.adapter.EmployeeClientAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.feign.adapter.NotificationClientAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.feign.adapter.OwnerClientAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.EmployeeClient;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.NotificationClient;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.OwnerClient;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.UserClient;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter.DishCategoryJpaAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter.DishJpaAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IDishCategoryEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IDishEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IDishCategoryRepository;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IDishRepository;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IOrderRepository;
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
    private final EmployeeClient employeeClient;
    private final UserClient userClient;
    private final NotificationClient notificationClient;
    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper  orderEntityMapper;

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

    @Bean
    public IEmployeeClientPort employeeClientPort() {
        return new EmployeeClientAdapter(employeeClient);
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, orderEntityMapper);
    }

    @Bean
    public INotificationPort notificationPort() {
        return new NotificationClientAdapter(userClient, notificationClient);
    }

    @Bean
    public IOrderServicePort orderServicePort() {
        return new OrderUseCase(orderPersistencePort(), authenticatedUserPort(), dishPersistencePort(), employeeClientPort(), notificationPort());
    }
}