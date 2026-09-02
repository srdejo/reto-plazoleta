package co.com.srdejo.plazoleta.application.handler;

import co.com.srdejo.plazoleta.application.dto.request.RestaurantRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.RestaurantResponseDto;

import java.util.List;

public interface IRestaurantHandler {

    void saveRestaurant(RestaurantRequestDto restaurantRequestDto);

    List<RestaurantResponseDto> getAllRestaurants();

    RestaurantResponseDto getRestaurant(Long id);
}