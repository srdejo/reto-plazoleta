package co.com.srdejo.plazoleta.application.handler;

import co.com.srdejo.plazoleta.application.dto.request.DishCategoryRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.DishCategoryResponseDto;

import java.util.List;

public interface IDishCategoryHandler {

    void saveDishCategory(DishCategoryRequestDto restaurantRequestDto);

    List<DishCategoryResponseDto> getAllDishCategories();
}