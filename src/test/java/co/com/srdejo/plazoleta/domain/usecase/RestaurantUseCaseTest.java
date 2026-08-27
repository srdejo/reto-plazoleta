package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOwnerException;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import co.com.srdejo.plazoleta.domain.spi.IOwnerClientPort;
import co.com.srdejo.plazoleta.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IOwnerClientPort ownerClientPort;

    private RestaurantUseCase restaurantUseCase;

    @BeforeEach
    void setUp() {
        restaurantUseCase = new RestaurantUseCase(restaurantPersistencePort, ownerClientPort);
    }

    private RestaurantModel restaurantModel() {
        return new RestaurantModel(null, "Pizzeria La Bella", "Cra 1 # 2-3", 10L,
                "+573005698325", "http://logo.png", "1234567890123");
    }

    @Test
    void saveRestaurant_ownerExists_persistsRestaurant() {
        RestaurantModel model = restaurantModel();
        when(ownerClientPort.existsOwner(model.getOwnerId())).thenReturn(true);

        restaurantUseCase.saveRestaurant(model);

        verify(restaurantPersistencePort).saveRestaurant(model);
    }

    @Test
    void saveRestaurant_ownerDoesNotExist_throwsInvalidOwnerException() {
        RestaurantModel model = restaurantModel();
        when(ownerClientPort.existsOwner(model.getOwnerId())).thenReturn(false);

        assertThatThrownBy(() -> restaurantUseCase.saveRestaurant(model))
                .isInstanceOf(InvalidOwnerException.class)
                .satisfies(ex -> assertThat(((InvalidOwnerException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.INVALID_OWNER_ID));

        verifyNoInteractions(restaurantPersistencePort);
    }

    @Test
    void getAllRestaurants_delegatesToPersistencePort() {
        List<RestaurantModel> restaurants = List.of(restaurantModel());
        when(restaurantPersistencePort.getAllRestaurants()).thenReturn(restaurants);

        List<RestaurantModel> result = restaurantUseCase.getAllRestaurants();

        assertThat(result).isEqualTo(restaurants);
    }
}
