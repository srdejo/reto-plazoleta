package co.com.srdejo.plazoleta.application.handler.impl;

import co.com.srdejo.plazoleta.application.dto.request.RestaurantRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.PageResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.RestaurantNameAndLogoResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.RestaurantResponseDto;
import co.com.srdejo.plazoleta.application.handler.IRestaurantHandler;
import co.com.srdejo.plazoleta.application.mapper.IRestaurantRequestMapper;
import co.com.srdejo.plazoleta.application.mapper.IRestaurantResponseMapper;
import co.com.srdejo.plazoleta.domain.api.IRestaurantServicePort;
import co.com.srdejo.plazoleta.domain.model.PageRequestModel;
import co.com.srdejo.plazoleta.domain.model.PageResultModel;
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
    public void saveRestaurant(RestaurantRequestDto restaurantRequestDto) {
        RestaurantModel restaurantModel = restaurantRequestMapper.toRestaurant(restaurantRequestDto);
        restaurantServicePort.saveRestaurant(restaurantModel);
    }

    @Override
    public PageResponseDto<RestaurantNameAndLogoResponseDto> getAllRestaurants(int page, int size, boolean ascending) {
        PageRequestModel pageRequestModel = new PageRequestModel(page, size, ascending);
        PageResultModel<RestaurantModel> pageResultModel = restaurantServicePort.getAllRestaurants(pageRequestModel);
        return new PageResponseDto<>(
                restaurantResponseMapper.toNameAndLogoResponseDtoList(pageResultModel.content()),
                pageResultModel.page(),
                pageResultModel.size(),
                pageResultModel.totalElements(),
                pageResultModel.totalPages());
    }

    @Override
    public RestaurantResponseDto getRestaurant(Long id) {
        return restaurantResponseMapper.toResponse(restaurantServicePort.getRestaurant(id));
    }
}