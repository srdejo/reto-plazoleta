package co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper;

import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.DishCategoryEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.DishEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IDishEntityMapper {

    @Mapping(target = "restaurant", source = "restaurantId", qualifiedByName = "mapRestaurant")
    @Mapping(target = "dishCategory", source = "dishCategoryId", qualifiedByName = "mapDishCategory")
    DishEntity toEntity(DishModel restaurantModel);
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "dishCategoryId", source = "dishCategory.id")
    DishModel toDishModel(DishEntity restaurantEntity);
    List<DishModel> toDishModelList(List<DishEntity> userEntityList);

    @Named("mapRestaurant")
    default RestaurantEntity mapRestaurant(Long id) {
        if (id == null) return null;
        RestaurantEntity entity = new RestaurantEntity();
        entity.setId(id);
        return entity;
    }

    @Named("mapDishCategory")
    default DishCategoryEntity mapDishCategory(Long id) {
        if (id == null) return null;
        DishCategoryEntity entity = new DishCategoryEntity();
        entity.setId(id);
        return entity;
    }
}