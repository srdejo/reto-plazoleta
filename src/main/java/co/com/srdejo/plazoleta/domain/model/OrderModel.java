package co.com.srdejo.plazoleta.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
    private String pin;
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
        this.orderDate = LocalDateTime.now(ZoneId.of("America/Bogota"));
        this.status = OrderStatus.PENDING;
        this.items = items;
    }

    public void markAsReady() {
        this.status = OrderStatus.READY;
        this.pin = String.valueOf(ThreadLocalRandom.current().nextInt(10000, 100000));
    }

}
