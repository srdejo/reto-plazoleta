package co.com.srdejo.plazoleta.domain.spi;

import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;

public interface IRestaurantPersistencePort {
    RestaurantModel saveRestaurant(RestaurantModel restaurantModel);

    PageResult<RestaurantModel> getAllRestaurants(PageRequest pageRequest);

    RestaurantModel getRestaurant(Long restaurantId);
}