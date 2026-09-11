package co.com.srdejo.plazoleta.domain.model;

import java.util.List;

public enum OrderStatus {
    PENDING,
    IN_PREPARATION,
    READY,
    DELIVERED;

    public static final List<OrderStatus> ACTIVE = List.of(PENDING, IN_PREPARATION, READY);
}
