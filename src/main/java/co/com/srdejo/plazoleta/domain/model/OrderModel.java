package co.com.srdejo.plazoleta.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderModel {

    private Long id;
    private Long customerId;
    private Long restaurantId;
    private Long chefId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItemModel> items;

    public OrderModel(
            Long customerId,
            Long restaurantId,
            Long chefId,
            List<OrderItemModel> items
    ) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.chefId = chefId;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.items = items;
    }

}
