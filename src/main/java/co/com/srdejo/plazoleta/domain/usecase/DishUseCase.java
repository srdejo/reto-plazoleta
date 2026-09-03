package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.api.IDishServicePort;
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

import java.util.List;

public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IOwnerClientPort ownerClientPort;
    private final IDishCategoryPersistencePort dishCategoryPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public DishUseCase(
            IDishPersistencePort dishPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            IOwnerClientPort ownerClientPort,
            IDishCategoryPersistencePort dishCategoryPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort
    ) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.ownerClientPort = ownerClientPort;
        this.dishCategoryPersistencePort = dishCategoryPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public void saveDish(DishModel dishModel) {
        Long ownerId = authenticatedUserPort.getAuthenticatedUserId();
        validateOwner(ownerId, dishModel);
        if (!dishCategoryPersistencePort.existsById(dishModel.getDishCategoryId())) {
            throw new InvalidDishCategoryException(ErrorCodesEnum.INVALID_DISH_CATEGORY_ID);
        }
        dishModel.setActive(true);
        dishPersistencePort.saveDish(dishModel);
    }

    @Override
    public List<DishModel> getAllDishes() {
        return dishPersistencePort.getAllDishes();
    }

    @Override
    public DishModel patchDish(DishModel dishModel) {
        Long ownerId = authenticatedUserPort.getAuthenticatedUserId();
        DishModel existingDish = dishPersistencePort.findById(dishModel.getId());
        validateOwner(ownerId, existingDish);
        existingDish.updatePriceAndDescription(dishModel.getPrice(), dishModel.getDescription());
        return dishPersistencePort.saveDish(existingDish);
    }

    private void validateOwner(Long ownerId, DishModel dishModel) {
        validateOwnerExists(ownerId);
        validateRestaurantOwnership(dishModel.getRestaurantId(), ownerId);
    }

    private void validateOwnerExists(Long ownerId) {
        if (!ownerClientPort.existsOwner(ownerId)) {
            throw new InvalidOwnerException(ErrorCodesEnum.INVALID_OWNER_ID);
        }
    }

    private void validateRestaurantOwnership(Long restaurantId, Long ownerId) {
        RestaurantModel restaurantModel = restaurantPersistencePort.getRestaurant(restaurantId);
        if (!restaurantModel.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException(ErrorCodesEnum.OWNER_NOT_AUTHORIZED);
        }
    }
}