package co.com.srdejo.plazoleta.application.mapper;

import co.com.srdejo.plazoleta.application.dto.response.RestaurantNameAndLogoResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.RestaurantResponseDto;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantResponseMapper {
    RestaurantResponseDto toResponse(RestaurantModel restaurantModel);

    RestaurantNameAndLogoResponseDto toNameAndLogoResponse(RestaurantModel restaurantModel);

    List<RestaurantNameAndLogoResponseDto> toNameAndLogoResponseDtoList(List<RestaurantModel> restaurantModelList);
}
