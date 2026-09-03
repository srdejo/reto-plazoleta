package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.model.DishModel;

import java.util.List;

public interface IDishServicePort {

    void saveDish(DishModel objectModel);

    List<DishModel> getAllDishes();

    DishModel patchDish(DishModel request);

    DishModel updateDishStatus(Long id, boolean enabled);
}