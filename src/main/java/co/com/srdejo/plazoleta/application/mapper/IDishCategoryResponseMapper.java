package co.com.srdejo.plazoleta.application.mapper;

import co.com.srdejo.plazoleta.application.dto.response.DishCategoryResponseDto;
import co.com.srdejo.plazoleta.domain.model.DishCategoryModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishCategoryResponseMapper {
    DishCategoryResponseDto toResponse(DishCategoryModel dishCategoryModel);

    List<DishCategoryResponseDto> toResponseList(List<DishCategoryModel> dishCategoryModelList);
}
