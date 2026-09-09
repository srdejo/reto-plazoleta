package co.com.srdejo.plazoleta.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {

    @NotNull
    private Long restaurantId;
    @NotNull
    private List<OrderItemRequestDto> items;

}
