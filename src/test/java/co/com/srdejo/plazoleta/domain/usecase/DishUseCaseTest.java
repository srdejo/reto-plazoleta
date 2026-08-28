package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOwnerException;
import co.com.srdejo.plazoleta.domain.exception.UnauthorizedException;
import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import co.com.srdejo.plazoleta.domain.spi.IDishPersistencePort;
import co.com.srdejo.plazoleta.domain.spi.IOwnerClientPort;
import co.com.srdejo.plazoleta.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishUseCaseTest {

    private static final Long OWNER_ID = 10L;
    private static final Long RESTAURANT_ID = 1L;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IOwnerClientPort ownerClientPort;

    private DishUseCase dishUseCase;

    @BeforeEach
    void setUp() {
        dishUseCase = new DishUseCase(dishPersistencePort, restaurantPersistencePort, ownerClientPort);
    }

    private DishModel dishModel() {
        return new DishModel(null, "Hamburguesa Mixta", new BigDecimal(15000), 1L, RESTAURANT_ID,
                "Hamburguesa con carne y pollo", "http://image.png", null);
    }

    private RestaurantModel restaurantModel(Long ownerId) {
        return new RestaurantModel(RESTAURANT_ID, "Pizzeria La Bella", "Cra 1 # 2-3", ownerId,
                "+573005698325", "http://logo.png", "1234567890123");
    }

    @Test
    void saveDish_ownerIsRestaurantOwner_persistsDishWithActiveTrue() {
        DishModel model = dishModel();
        when(ownerClientPort.existsOwner(OWNER_ID)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurant(RESTAURANT_ID)).thenReturn(restaurantModel(OWNER_ID));

        dishUseCase.saveDish(model, OWNER_ID);

        verify(dishPersistencePort).saveDish(model);
        assertThat(model.getActive()).isTrue();
    }

    @Test
    void saveDish_ownerExistsButIsNotRestaurantOwner_throwsUnauthorizedException() {
        DishModel model = dishModel();
        when(ownerClientPort.existsOwner(OWNER_ID)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurant(RESTAURANT_ID)).thenReturn(restaurantModel(999L));

        assertThatThrownBy(() -> dishUseCase.saveDish(model, OWNER_ID))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> assertThat(((UnauthorizedException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.OWNER_NOT_AUTHORIZED));

        verifyNoInteractions(dishPersistencePort);
    }

    @Test
    void saveDish_ownerDoesNotExist_throwsInvalidOwnerException() {
        DishModel model = dishModel();
        when(ownerClientPort.existsOwner(OWNER_ID)).thenReturn(false);

        assertThatThrownBy(() -> dishUseCase.saveDish(model, OWNER_ID))
                .isInstanceOf(InvalidOwnerException.class)
                .satisfies(ex -> assertThat(((InvalidOwnerException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.INVALID_OWNER_ID));

        verifyNoInteractions(restaurantPersistencePort, dishPersistencePort);
    }

    @Test
    void saveDish_restaurantDoesNotExist_propagatesLookupFailureAndDoesNotPersist() {
        DishModel model = dishModel();
        when(ownerClientPort.existsOwner(OWNER_ID)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurant(RESTAURANT_ID))
                .thenThrow(new RuntimeException("Restaurant not found"));

        assertThatThrownBy(() -> dishUseCase.saveDish(model, OWNER_ID))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(dishPersistencePort);
    }
}
