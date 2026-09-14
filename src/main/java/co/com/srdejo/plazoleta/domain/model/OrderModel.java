package co.com.srdejo.plazoleta.domain.model;

import co.com.srdejo.plazoleta.domain.exception.InvalidOrderPinException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import co.com.srdejo.plazoleta.domain.exception.NotYetReadyOrderException;

import static co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum.*;

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

    public void markAsDelivered(String pin) {
        if (this.status != OrderStatus.READY) {
            throw new NotYetReadyOrderException(NOT_YET_READY_EXCEPTION);
        }

        if (!Objects.equals(this.pin, pin)) {
            throw new InvalidOrderPinException(INVALID_PIN_EXCEPTION);
        }

        this.status = OrderStatus.DELIVERED;
    }
}
