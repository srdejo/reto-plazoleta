package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.api.IRestaurantServicePort;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOwnerException;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import co.com.srdejo.plazoleta.domain.spi.IOwnerClientPort;
import co.com.srdejo.plazoleta.domain.spi.IRestaurantPersistencePort;

import java.util.List;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IOwnerClientPort ownerClientPort;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort, IOwnerClientPort ownerClientPort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.ownerClientPort = ownerClientPort;
    }

    @Override
    public void saveRestaurant(RestaurantModel restaurantModel) {
        if (!ownerClientPort.existsOwner(restaurantModel.getOwnerId())){
            throw new InvalidOwnerException(ErrorCodesEnum.INVALID_OWNER_ID);
        }
        restaurantPersistencePort.saveRestaurant(restaurantModel);
    }

    @Override
    public List<RestaurantModel> getAllRestaurants() {
        return restaurantPersistencePort.getAllRestaurants();
    }
}