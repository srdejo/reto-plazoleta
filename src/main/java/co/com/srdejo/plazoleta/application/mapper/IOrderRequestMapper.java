package co.com.srdejo.plazoleta.application.mapper;

import co.com.srdejo.plazoleta.application.dto.request.OrderItemRequestDto;
import co.com.srdejo.plazoleta.application.dto.request.OrderRequestDto;
import co.com.srdejo.plazoleta.domain.model.OrderItemModel;
import co.com.srdejo.plazoleta.domain.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IOrderRequestMapper {

    OrderModel toModel(OrderRequestDto request);

    List<OrderItemModel> toOrderItemModelList(
            List<OrderItemRequestDto> items
    );

    OrderItemModel toOrderItemModel(OrderItemRequestDto item);
}
