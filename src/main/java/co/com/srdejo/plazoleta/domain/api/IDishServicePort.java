package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.model.PageRequestModel;
import co.com.srdejo.plazoleta.domain.model.PageResultModel;

public interface IDishServicePort {

    void saveDish(DishModel objectModel);

    DishModel patchDish(DishModel request);

    DishModel updateDishStatus(Long id, boolean enabled);

    PageResultModel<DishModel> getAllDishes(Long categoryId, PageRequestModel pageRequestModel);
}