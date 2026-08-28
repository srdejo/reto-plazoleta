package co.com.srdejo.plazoleta.application.mapper;

import co.com.srdejo.plazoleta.application.dto.request.DishPatchRequestDto;
import co.com.srdejo.plazoleta.application.dto.request.DishRequestDto;
import co.com.srdejo.plazoleta.domain.model.DishModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface IDishRequestMapper {
    DishModel toDish(DishRequestDto dishRequestDto);

    @Mapping(target = "id", source = "id")
    void patch(Long id, DishPatchRequestDto dishPatchRequestDto, @MappingTarget DishModel dishModel);
}
