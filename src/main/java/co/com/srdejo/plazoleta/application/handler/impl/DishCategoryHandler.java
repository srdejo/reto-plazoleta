package co.com.srdejo.plazoleta.application.handler.impl;

import co.com.srdejo.plazoleta.application.dto.request.DishCategoryRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.DishCategoryResponseDto;
import co.com.srdejo.plazoleta.application.handler.IDishCategoryHandler;
import co.com.srdejo.plazoleta.application.mapper.IDishCategoryRequestMapper;
import co.com.srdejo.plazoleta.application.mapper.IDishCategoryResponseMapper;
import co.com.srdejo.plazoleta.domain.api.IDishCategoryServicePort;
import co.com.srdejo.plazoleta.domain.model.DishCategoryModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DishCategoryHandler implements IDishCategoryHandler {

    private final IDishCategoryServicePort dishCategoryServicePort;
    private final IDishCategoryRequestMapper dishCategoryRequestMapper;
    private final IDishCategoryResponseMapper dishCategoryResponseMapper;

    @Override
    public void saveDishCategory(DishCategoryRequestDto dishCategoryRequestDto) {
        DishCategoryModel dishCategoryModel = dishCategoryRequestMapper.toDishCategory(dishCategoryRequestDto);
        dishCategoryServicePort.saveDishCategory(dishCategoryModel);
    }

    @Override
    public List<DishCategoryResponseDto> getAllDishCategories() {
        return dishCategoryResponseMapper.toResponseList(dishCategoryServicePort.getAllDishCategories());
    }
}