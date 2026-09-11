package co.com.srdejo.plazoleta.application.handler.impl;

import co.com.srdejo.plazoleta.application.dto.request.DishPatchRequestDto;
import co.com.srdejo.plazoleta.application.dto.request.DishRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.DishResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.PageResponseDto;
import co.com.srdejo.plazoleta.application.handler.IDishHandler;
import co.com.srdejo.plazoleta.application.mapper.IDishRequestMapper;
import co.com.srdejo.plazoleta.application.mapper.IDishResponseMapper;
import co.com.srdejo.plazoleta.domain.api.IDishServicePort;
import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;

    @Override
    public void saveDish(DishRequestDto dishRequestDto) {
        DishModel dishModel = dishRequestMapper.toDish(dishRequestDto);
        dishServicePort.saveDish(dishModel);
    }

    @Override
    public DishResponseDto patchDish(Long id, DishPatchRequestDto request) {
        DishModel dishModel = new DishModel();
        dishModel.setId(id);
        dishRequestMapper.patch(id, request,  dishModel);
        return dishResponseMapper.toResponse(dishServicePort.patchDish(dishModel));
    }

    @Override
    public DishResponseDto updateDishStatus(Long id, boolean enabled) {
        return dishResponseMapper.toResponse(dishServicePort.updateDishStatus(id, enabled));
    }

    @Override
    public PageResponseDto<DishResponseDto> getAllDishes(int page, int size, Long categoryId, boolean ascending) {
        PageRequest pageRequest = new PageRequest(page, size, ascending);
        PageResult<DishModel> pageResult = dishServicePort.getAllDishes(categoryId, pageRequest);
        return new PageResponseDto<>(
                dishResponseMapper.toResponseList(pageResult.content()),
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages());
    }
}