package co.com.srdejo.plazoleta.application.handler.impl;

import co.com.srdejo.plazoleta.application.dto.request.RestaurantRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.RestaurantResponseDto;
import co.com.srdejo.plazoleta.application.handler.IRestaurantHandler;
import co.com.srdejo.plazoleta.application.mapper.IRestaurantRequestMapper;
import co.com.srdejo.plazoleta.application.mapper.IRestaurantResponseMapper;
import co.com.srdejo.plazoleta.domain.api.IRestaurantServicePort;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort objectServicePort;
    private final IRestaurantRequestMapper objectRequestMapper;
    private final IRestaurantResponseMapper objectResponseMapper;

    @Override
    public void saveRestaurant(RestaurantRequestDto restaurantRequestDto) {
        RestaurantModel objectModel = objectRequestMapper.toRestaurant(restaurantRequestDto);
        objectServicePort.saveRestaurant(objectModel);
    }

    @Override
    public List<RestaurantResponseDto> getAllRestaurants() {
        return objectResponseMapper.toResponseList(objectServicePort.getAllRestaurants());
    }
}