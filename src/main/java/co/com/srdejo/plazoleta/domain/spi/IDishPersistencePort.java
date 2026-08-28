package co.com.srdejo.plazoleta.domain.spi;

import co.com.srdejo.plazoleta.domain.model.DishModel;

import java.util.List;

public interface IDishPersistencePort {
    void saveDish(DishModel restaurantModel);

    List<DishModel> getAllDishes();
}