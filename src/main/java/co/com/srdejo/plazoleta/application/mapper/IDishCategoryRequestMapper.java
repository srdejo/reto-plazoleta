package co.com.srdejo.plazoleta.application.mapper;

import co.com.srdejo.plazoleta.application.dto.request.DishCategoryRequestDto;
import co.com.srdejo.plazoleta.domain.model.DishCategoryModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishCategoryRequestMapper {
    DishCategoryModel toDishCategory(DishCategoryRequestDto dishCategoryRequestDto);
}
