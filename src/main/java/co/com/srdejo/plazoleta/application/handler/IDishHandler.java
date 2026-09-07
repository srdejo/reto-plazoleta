package co.com.srdejo.plazoleta.application.handler;

import co.com.srdejo.plazoleta.application.dto.request.DishPatchRequestDto;
import co.com.srdejo.plazoleta.application.dto.request.DishRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.DishResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.PageResponseDto;

public interface IDishHandler {

    void saveDish(DishRequestDto dishRequestDto);

    DishResponseDto patchDish(Long id, DishPatchRequestDto request);

    DishResponseDto updateDishStatus(Long id, boolean enabled);

    PageResponseDto<DishResponseDto> getAllDishes(int page, int size, Long categoryId, boolean b);
}