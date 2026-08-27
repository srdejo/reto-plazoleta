package co.com.srdejo.plazoleta.domain.spi;

import co.com.srdejo.plazoleta.domain.model.RestaurantModel;

import java.util.List;

public interface IRestaurantPersistencePort {
    RestaurantModel saveRestaurant(RestaurantModel restaurantModel);

    List<RestaurantModel> getAllRestaurants();
}