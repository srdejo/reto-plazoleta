package co.com.srdejo.plazoleta.application.dto.response;


import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDto {

    private Long id;
    private Long restaurantId;
    private OrderStatus status;

}
