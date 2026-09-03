package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidDishCategoryException;
import co.com.srdejo.plazoleta.domain.exception.InvalidOwnerException;
import co.com.srdejo.plazoleta.domain.exception.UnauthorizedException;
import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import co.com.srdejo.plazoleta.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.plazoleta.domain.spi.IDishCategoryPersistencePort;
import co.com.srdejo.plazoleta.domain.spi.IDishPersistencePort;
import co.com.srdejo.plazoleta.domain.spi.IOwnerClientPort;
import co.com.srdejo.plazoleta.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @Mock
    private IDishCategoryPersistencePort dishCategoryPersistencePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    private DishUseCase dishUseCase;

    @BeforeEach
    void setUp() {
        dishUseCase = new DishUseCase(dishPersistencePort, restaurantPersistencePort, ownerClientPort, dishCategoryPersistencePort, authenticatedUserPort);
        lenient().when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(OWNER_ID);
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
        when(dishCategoryPersistencePort.existsById(model.getDishCategoryId())).thenReturn(true);

        dishUseCase.saveDish(model);

        verify(dishPersistencePort).saveDish(model);
        assertThat(model.getActive()).isTrue();
    }

    @Test
    void saveDish_dishCategoryDoesNotExist_throwsInvalidDishCategoryException() {
        DishModel model = dishModel();
        when(ownerClientPort.existsOwner(OWNER_ID)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurant(RESTAURANT_ID)).thenReturn(restaurantModel(OWNER_ID));
        when(dishCategoryPersistencePort.existsById(model.getDishCategoryId())).thenReturn(false);

        assertThatThrownBy(() -> dishUseCase.saveDish(model))
                .isInstanceOf(InvalidDishCategoryException.class)
                .satisfies(ex -> assertThat(((InvalidDishCategoryException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.INVALID_DISH_CATEGORY_ID));

        verifyNoInteractions(dishPersistencePort);
    }

    @Test
    void saveDish_ownerExistsButIsNotRestaurantOwner_throwsUnauthorizedException() {
        DishModel model = dishModel();
        when(ownerClientPort.existsOwner(OWNER_ID)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurant(RESTAURANT_ID)).thenReturn(restaurantModel(999L));

        assertThatThrownBy(() -> dishUseCase.saveDish(model))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> assertThat(((UnauthorizedException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.OWNER_NOT_AUTHORIZED));

        verifyNoInteractions(dishPersistencePort);
    }

    @Test
    void saveDish_ownerDoesNotExist_throwsInvalidOwnerException() {
        DishModel model = dishModel();
        when(ownerClientPort.existsOwner(OWNER_ID)).thenReturn(false);

        assertThatThrownBy(() -> dishUseCase.saveDish(model))
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

        assertThatThrownBy(() -> dishUseCase.saveDish(model))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(dishPersistencePort);
    }

    @Test
    void patchDish_ownerIsRestaurantOwner_updatesPriceAndDescription() {
        DishModel existingDish = new DishModel(5L, "Hamburguesa Mixta", new BigDecimal(15000), 1L, RESTAURANT_ID,
                "Hamburguesa con carne y pollo", "http://image.png", true);
        DishModel patchRequest = new DishModel(5L, null, new BigDecimal(18000), null, RESTAURANT_ID,
                "Hamburguesa con carne, pollo y tocineta", null, null);

        when(dishPersistencePort.findById(5L)).thenReturn(existingDish);
        when(ownerClientPort.existsOwner(OWNER_ID)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurant(RESTAURANT_ID)).thenReturn(restaurantModel(OWNER_ID));
        when(dishPersistencePort.saveDish(existingDish)).thenReturn(existingDish);

        DishModel result = dishUseCase.patchDish(patchRequest);

        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal(18000));
        assertThat(result.getDescription()).isEqualTo("Hamburguesa con carne, pollo y tocineta");
        verify(dishPersistencePort).saveDish(existingDish);
    }

    @Test
    void patchDish_ownerNotAuthorizedForDishRestaurant_throwsUnauthorizedException() {
        Long otherRestaurantId = 2L;
        DishModel existingDish = new DishModel(5L, "Hamburguesa Mixta", new BigDecimal(15000), 1L, RESTAURANT_ID,
                "Hamburguesa con carne y pollo", "http://image.png", true);
        DishModel patchRequest = new DishModel(5L, null, new BigDecimal(18000), null, otherRestaurantId,
                "Nueva descripcion", null, null);

        when(dishPersistencePort.findById(5L)).thenReturn(existingDish);
        when(ownerClientPort.existsOwner(OWNER_ID)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurant(RESTAURANT_ID)).thenReturn(restaurantModel(999L));

        assertThatThrownBy(() -> dishUseCase.patchDish(patchRequest))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> assertThat(((UnauthorizedException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.OWNER_NOT_AUTHORIZED));

        verify(restaurantPersistencePort).getRestaurant(RESTAURANT_ID);
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void patchDish_ownerDoesNotExist_throwsInvalidOwnerExceptionAndDoesNotPersist() {
        DishModel existingDish = new DishModel(5L, "Hamburguesa Mixta", new BigDecimal(15000), 1L, RESTAURANT_ID,
                "Hamburguesa con carne y pollo", "http://image.png", true);
        DishModel patchRequest = new DishModel(5L, null, new BigDecimal(18000), null, RESTAURANT_ID,
                "Nueva descripcion", null, null);

        when(dishPersistencePort.findById(5L)).thenReturn(existingDish);
        when(ownerClientPort.existsOwner(OWNER_ID)).thenReturn(false);

        assertThatThrownBy(() -> dishUseCase.patchDish(patchRequest))
                .isInstanceOf(InvalidOwnerException.class)
                .satisfies(ex -> assertThat(((InvalidOwnerException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.INVALID_OWNER_ID));

        verifyNoInteractions(restaurantPersistencePort);
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void updateDishStatus_ownerIsRestaurantOwner_updatesActiveStatus(boolean enabled) {
        DishModel existingDish = dishModel();

        when(authenticatedUserPort.getAuthenticatedUserId())
                .thenReturn(OWNER_ID);
        when(dishPersistencePort.findById(5L))
                .thenReturn(existingDish);
        when(ownerClientPort.existsOwner(OWNER_ID))
                .thenReturn(true);
        when(restaurantPersistencePort.getRestaurant(RESTAURANT_ID))
                .thenReturn(restaurantModel(OWNER_ID));
        when(dishPersistencePort.saveDish(existingDish))
                .thenReturn(existingDish);

        DishModel result = dishUseCase.updateDishStatus(5L, enabled);

        assertThat(result.getActive()).isEqualTo(enabled);

        verify(dishPersistencePort).findById(5L);
        verify(ownerClientPort).existsOwner(OWNER_ID);
        verify(restaurantPersistencePort).getRestaurant(RESTAURANT_ID);
        verify(dishPersistencePort).saveDish(existingDish);
    }

    @Test
    void updateDishStatus_ownerNotAuthorizedForDishRestaurant_throwsUnauthorizedExceptionAndDoesNotPersist() {
        DishModel existingDish = dishModel();

        when(authenticatedUserPort.getAuthenticatedUserId())
                .thenReturn(OWNER_ID);
        when(dishPersistencePort.findById(5L))
                .thenReturn(existingDish);
        when(ownerClientPort.existsOwner(OWNER_ID))
                .thenReturn(true);
        when(restaurantPersistencePort.getRestaurant(RESTAURANT_ID))
                .thenReturn(restaurantModel(999L));

        assertThatThrownBy(() -> dishUseCase.updateDishStatus(5L, false))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> assertThat(((UnauthorizedException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.OWNER_NOT_AUTHORIZED));

        verify(restaurantPersistencePort).getRestaurant(RESTAURANT_ID);
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void updateDishStatus_ownerDoesNotExist_throwsInvalidOwnerExceptionAndDoesNotPersist() {
        DishModel existingDish = dishModel();

        when(authenticatedUserPort.getAuthenticatedUserId())
                .thenReturn(OWNER_ID);
        when(dishPersistencePort.findById(5L))
                .thenReturn(existingDish);
        when(ownerClientPort.existsOwner(OWNER_ID))
                .thenReturn(false);

        assertThatThrownBy(() -> dishUseCase.updateDishStatus(5L, false))
                .isInstanceOf(InvalidOwnerException.class)
                .satisfies(ex -> assertThat(((InvalidOwnerException) ex).getError())
                        .isEqualTo(ErrorCodesEnum.INVALID_OWNER_ID));

        verifyNoInteractions(restaurantPersistencePort);
        verify(dishPersistencePort, never()).saveDish(any());
    }
}
