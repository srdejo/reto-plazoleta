package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;

public interface IRestaurantServicePort {

    RestaurantModel saveRestaurant(RestaurantModel objectModel);

    PageResult<RestaurantModel> getAllRestaurants(PageRequest pageRequest);

    RestaurantModel getRestaurant(Long id);
}