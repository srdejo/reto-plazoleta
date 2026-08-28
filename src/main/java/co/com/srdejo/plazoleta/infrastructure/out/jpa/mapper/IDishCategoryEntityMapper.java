package co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper;

import co.com.srdejo.plazoleta.domain.model.DishCategoryModel;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.DishCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IDishCategoryEntityMapper {

    DishCategoryEntity toEntity(DishCategoryModel restaurantModel);
    DishCategoryModel toDishCategoryModel(DishCategoryEntity restaurantEntity);
    List<DishCategoryModel> toDishCategoryModelList(List<DishCategoryEntity> userEntityList);
}