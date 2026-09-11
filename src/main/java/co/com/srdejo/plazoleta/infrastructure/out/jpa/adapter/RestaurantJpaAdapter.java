package co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter;

import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import co.com.srdejo.plazoleta.domain.spi.IRestaurantPersistencePort;
import co.com.srdejo.plazoleta.infrastructure.exception.NoDataFoundException;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.RestaurantEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    public PageResult<RestaurantModel> getAllRestaurants(PageRequest pageRequest) {
        Sort sort = Sort.by(pageRequest.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, "name");
        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size(), sort);

        Page<RestaurantEntity> entityPage = restaurantRepository.findAll(pageable);
        if (entityPage.isEmpty()) {
            throw new NoDataFoundException();
        }

        return new PageResult<>(
                restaurantEntityMapper.toRestaurantModelList(entityPage.getContent()),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages());
    }

    @Override
    public RestaurantModel getRestaurant(Long restaurantId) {
        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId).orElseThrow(NoDataFoundException::new);
        return restaurantEntityMapper.toRestaurantModel(restaurantEntity);
    }
}