package co.com.srdejo.plazoleta.domain.spi;

import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;

public interface IDishPersistencePort {
    DishModel saveDish(DishModel restaurantModel);

    DishModel findById(Long id);

    PageResult<DishModel> getAllDishes(Long categoryId, PageRequest pageRequest);
}