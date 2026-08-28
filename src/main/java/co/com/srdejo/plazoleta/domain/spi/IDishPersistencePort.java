package co.com.srdejo.plazoleta.domain.spi;

import co.com.srdejo.plazoleta.domain.model.DishModel;

import java.util.List;

public interface IDishPersistencePort {
    DishModel saveDish(DishModel restaurantModel);

    List<DishModel> getAllDishes();

    DishModel findById(Long id);
}