package co.com.srdejo.plazoleta.application.handler;

import co.com.srdejo.plazoleta.application.dto.request.RestaurantRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.PageResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.RestaurantNameAndLogoResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.RestaurantResponseDto;

public interface IRestaurantHandler {

    void saveRestaurant(RestaurantRequestDto restaurantRequestDto);

    PageResponseDto<RestaurantNameAndLogoResponseDto> getAllRestaurants(int page, int size, boolean ascending);

    RestaurantResponseDto getRestaurant(Long id);
}