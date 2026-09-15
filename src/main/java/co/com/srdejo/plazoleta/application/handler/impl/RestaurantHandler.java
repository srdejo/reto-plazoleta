package co.com.srdejo.plazoleta.application.handler.impl;

import co.com.srdejo.plazoleta.application.dto.request.RestaurantRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.PageResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.RestaurantNameAndLogoResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.RestaurantResponseDto;
import co.com.srdejo.plazoleta.application.handler.IRestaurantHandler;
import co.com.srdejo.plazoleta.application.mapper.IRestaurantRequestMapper;
import co.com.srdejo.plazoleta.application.mapper.IRestaurantResponseMapper;
import co.com.srdejo.plazoleta.domain.api.IRestaurantServicePort;
import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;

    @Override
    public RestaurantResponseDto saveRestaurant(RestaurantRequestDto restaurantRequestDto) {
        RestaurantModel restaurantModel = restaurantRequestMapper.toRestaurant(restaurantRequestDto);
        RestaurantModel savedRestaurantModel = restaurantServicePort.saveRestaurant(restaurantModel);
        return restaurantResponseMapper.toResponse(savedRestaurantModel);
    }

    @Override
    public PageResponseDto<RestaurantNameAndLogoResponseDto> getAllRestaurants(int page, int size, boolean ascending) {
        PageRequest pageRequest = new PageRequest(page, size, ascending);
        PageResult<RestaurantModel> pageResult = restaurantServicePort.getAllRestaurants(pageRequest);
        return new PageResponseDto<>(
                restaurantResponseMapper.toNameAndLogoResponseDtoList(pageResult.content()),
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages());
    }

    @Override
    public RestaurantResponseDto getRestaurant(Long id) {
        return restaurantResponseMapper.toResponse(restaurantServicePort.getRestaurant(id));
    }
}