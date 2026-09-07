package co.com.srdejo.plazoleta.domain.spi;

import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.model.PageRequestModel;
import co.com.srdejo.plazoleta.domain.model.PageResultModel;

import java.util.List;

public interface IDishPersistencePort {
    DishModel saveDish(DishModel restaurantModel);

    DishModel findById(Long id);

    PageResultModel<DishModel> getAllDishes(Long categoryId, PageRequestModel pageRequestModel);
}