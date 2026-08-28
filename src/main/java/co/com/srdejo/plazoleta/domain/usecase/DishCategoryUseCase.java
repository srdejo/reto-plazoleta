package co.com.srdejo.plazoleta.domain.usecase;

import co.com.srdejo.plazoleta.domain.api.IDishCategoryServicePort;
import co.com.srdejo.plazoleta.domain.model.DishCategoryModel;
import co.com.srdejo.plazoleta.domain.spi.IDishCategoryPersistencePort;

import java.util.List;

public class DishCategoryUseCase implements IDishCategoryServicePort {

    private final IDishCategoryPersistencePort dishCategoryPersistencePort;

    public DishCategoryUseCase(IDishCategoryPersistencePort dishCategoryPersistencePort) {
        this.dishCategoryPersistencePort = dishCategoryPersistencePort;
    }

    @Override
    public void saveDishCategory(DishCategoryModel dishCategoryModel) {
        dishCategoryPersistencePort.saveDishCategory(dishCategoryModel);
    }

    @Override
    public List<DishCategoryModel> getAllDishCategories() {
        return dishCategoryPersistencePort.getAllDishCategories();
    }
}