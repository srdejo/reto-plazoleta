package co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter;

import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import co.com.srdejo.plazoleta.domain.spi.IRestaurantPersistencePort;
import co.com.srdejo.plazoleta.infrastructure.exception.NoDataFoundException;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.RestaurantEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;


    @Override
    public RestaurantModel saveRestaurant(RestaurantModel restaurantModel) {
        RestaurantEntity restaurantEntity = restaurantRepository.save(restaurantEntityMapper.toEntity(restaurantModel));
        return restaurantEntityMapper.toRestaurantModel(restaurantEntity);
    }

    @Override
    public List<RestaurantModel> getAllRestaurants() {
        List<RestaurantEntity> entityList = restaurantRepository.findAll();
        if (entityList.isEmpty()) {
            throw new NoDataFoundException();
        }
        return restaurantEntityMapper.toRestaurantModelList(entityList);
    }
}