package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.model.DishModel;

import java.util.List;

public interface IDishServicePort {

    void saveDish(DishModel objectModel, Long ownerId);

    List<DishModel> getAllDishes();

    DishModel patchDish(DishModel request, Long ownerId);
}