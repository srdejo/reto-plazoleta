package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.api.IDishServicePort;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidDishCategoryException;
import co.com.srdejo.plazoleta.domain.exception.InvalidOwnerException;
import co.com.srdejo.plazoleta.domain.exception.UnauthorizedException;
import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
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

    public DishUseCase(
            IDishPersistencePort dishPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            IOwnerClientPort ownerClientPort,
            IDishCategoryPersistencePort dishCategoryPersistencePort
    ) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.ownerClientPort = ownerClientPort;
        this.dishCategoryPersistencePort = dishCategoryPersistencePort;
    }

    @Override
    public void saveDish(DishModel dishModel, Long ownerId) {
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
    public DishModel patchDish(DishModel dishModel, Long ownerId) {
        DishModel existingDish = dishPersistencePort.findById(dishModel.getId());
        validateOwner(ownerId, existingDish);
        existingDish.patch(dishModel.getPrice(), dishModel.getDescription());
        return dishPersistencePort.saveDish(existingDish);
    }

    private void validateOwner(Long ownerId, DishModel dishModel) {
        if (!ownerClientPort.existsOwner(ownerId)){
            throw new InvalidOwnerException(ErrorCodesEnum.INVALID_OWNER_ID);
        }

        RestaurantModel restaurantModel = restaurantPersistencePort.getRestaurant(dishModel.getRestaurantId());
        if(!restaurantModel.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException(ErrorCodesEnum.OWNER_NOT_AUTHORIZED);
        }
    }
}