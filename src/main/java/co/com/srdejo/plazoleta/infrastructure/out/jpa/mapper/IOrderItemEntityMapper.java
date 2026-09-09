package co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper;

import co.com.srdejo.plazoleta.domain.model.OrderItemModel;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.DishEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = IOrderItemEntityMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IOrderItemEntityMapper {

    @Mapping(target = "dish", source = "dishId", qualifiedByName = "mapDish")
    OrderItemEntity toEntity(OrderItemModel orderItemModel);

    @Mapping(target = "dishId", source = "dish.id")
    OrderItemModel toOrderItemModel(OrderItemEntity orderItemEntity);

    List<OrderItemModel> toOrderItemModelList(List<OrderItemEntity> orderItemEntityList);

    @Named("mapDish")
    default DishEntity mapDish(Long id) {
        if (id == null) {
            return null;
        }

        DishEntity entity = new DishEntity();
        entity.setId(id);
        return entity;
    }
}