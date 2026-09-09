package co.com.srdejo.plazoleta.application.mapper;

import co.com.srdejo.plazoleta.application.dto.response.FullOrderResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.OrderResponseDto;
import co.com.srdejo.plazoleta.domain.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IOrderResponseMapper {

    OrderResponseDto toOrderResponseDto(OrderModel order);

    List<FullOrderResponseDto> toResponseList(List<OrderModel> content);
}
