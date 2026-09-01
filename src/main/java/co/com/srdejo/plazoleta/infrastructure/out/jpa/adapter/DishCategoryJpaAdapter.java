package co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter;

import co.com.srdejo.plazoleta.domain.model.DishCategoryModel;
import co.com.srdejo.plazoleta.domain.spi.IDishCategoryPersistencePort;
import co.com.srdejo.plazoleta.infrastructure.exception.NoDataFoundException;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.DishCategoryEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IDishCategoryEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IDishCategoryRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DishCategoryJpaAdapter implements IDishCategoryPersistencePort {

    private final IDishCategoryRepository restaurantRepository;
    private final IDishCategoryEntityMapper restaurantEntityMapper;


    @Override
    public void saveDishCategory(DishCategoryModel restaurantModel) {
        DishCategoryEntity restaurantEntity = restaurantRepository.save(restaurantEntityMapper.toEntity(restaurantModel));
        restaurantEntityMapper.toDishCategoryModel(restaurantEntity);
    }

    @Override
    public List<DishCategoryModel> getAllDishCategories() {
        List<DishCategoryEntity> entityList = restaurantRepository.findAll();
        if (entityList.isEmpty()) {
            throw new NoDataFoundException();
        }
        return restaurantEntityMapper.toDishCategoryModelList(entityList);
    }

    @Override
    public boolean existsById(Long id) {
        return restaurantRepository.existsById(id);
    }
}