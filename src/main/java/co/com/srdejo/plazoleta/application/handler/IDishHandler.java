package co.com.srdejo.plazoleta.application.handler;

import co.com.srdejo.plazoleta.application.dto.request.DishPatchRequestDto;
import co.com.srdejo.plazoleta.application.dto.request.DishRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.DishResponseDto;

import java.util.List;

public interface IDishHandler {

    void saveDish(DishRequestDto dishRequestDto);

    List<DishResponseDto> getAllDishes();

    DishResponseDto patchDish(Long id, DishPatchRequestDto request);

    DishResponseDto updateDishStatus(Long id, boolean enabled);
}