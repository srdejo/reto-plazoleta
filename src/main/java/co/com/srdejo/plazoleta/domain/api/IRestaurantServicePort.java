package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.model.RestaurantModel;

import java.util.List;

public interface IRestaurantServicePort {

    void saveRestaurant(RestaurantModel objectModel);

    List<RestaurantModel> getAllRestaurants();

    RestaurantModel getRestaurant(Long id);
}