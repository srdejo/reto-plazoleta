package co.com.srdejo.plazoleta.application.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DishPatchRequestDto {

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 0)
    private BigDecimal price;
    @NotBlank
    private String description;
}
