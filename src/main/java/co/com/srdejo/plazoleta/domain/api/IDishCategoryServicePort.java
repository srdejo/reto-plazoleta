package co.com.srdejo.plazoleta.domain.api;

import co.com.srdejo.plazoleta.domain.model.DishCategoryModel;

import java.util.List;

public interface IDishCategoryServicePort {

    void saveDishCategory(DishCategoryModel objectModel);

    List<DishCategoryModel> getAllDishCategories();
}