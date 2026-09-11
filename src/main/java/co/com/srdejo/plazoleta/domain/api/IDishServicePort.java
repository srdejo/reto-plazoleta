package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.utils.PageRequest;
import co.com.srdejo.plazoleta.domain.utils.PageResult;

public interface IDishServicePort {

    void saveDish(DishModel objectModel);

    DishModel patchDish(DishModel request);

    DishModel updateDishStatus(Long id, boolean enabled);

    PageResult<DishModel> getAllDishes(Long categoryId, PageRequest pageRequest);
}