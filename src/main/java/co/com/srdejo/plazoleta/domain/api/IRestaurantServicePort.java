package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.model.PageRequestModel;
import co.com.srdejo.plazoleta.domain.model.PageResultModel;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;

public interface IRestaurantServicePort {

    void saveRestaurant(RestaurantModel objectModel);

    PageResultModel<RestaurantModel> getAllRestaurants(PageRequestModel pageRequestModel);

    RestaurantModel getRestaurant(Long id);
}