package co.com.srdejo.plazoleta.application.mapper;

import co.com.srdejo.plazoleta.application.dto.request.RestaurantRequestDto;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantRequestMapper {
    RestaurantModel toRestaurant(RestaurantRequestDto restaurantRequestDto);
}
