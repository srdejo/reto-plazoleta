package co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter;

import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.spi.IDishPersistencePort;
import co.com.srdejo.plazoleta.infrastructure.exception.NoDataFoundException;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.DishEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IDishEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository restaurantRepository;
    private final IDishEntityMapper restaurantEntityMapper;


    @Override
    public DishModel saveDish(DishModel restaurantModel) {
        DishEntity restaurantEntity = restaurantRepository.save(restaurantEntityMapper.toEntity(restaurantModel));
        return restaurantEntityMapper.toDishModel(restaurantEntity);
    }

    @Override
    public List<DishModel> getAllDishes() {
        List<DishEntity> entityList = restaurantRepository.findAll();
        if (entityList.isEmpty()) {
            throw new NoDataFoundException();
        }
        return restaurantEntityMapper.toDishModelList(entityList);
    }

    @Override
    public DishModel findById(Long id) {
        DishEntity dishEntity = restaurantRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);
        return restaurantEntityMapper.toDishModel(dishEntity);
    }
}