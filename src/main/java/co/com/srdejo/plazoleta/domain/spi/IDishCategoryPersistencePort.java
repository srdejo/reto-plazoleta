package co.com.srdejo.plazoleta.domain.spi;

import co.com.srdejo.plazoleta.domain.model.DishCategoryModel;

import java.util.List;

public interface IDishCategoryPersistencePort {
    void saveDishCategory(DishCategoryModel restaurantModel);

    List<DishCategoryModel> getAllDishCategories();
}