package co.com.srdejo.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DishRequestDto {
    @NotBlank
    @Schema(example = "Hamburguesa Mixta")
    private String name;
    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 0)
    private BigDecimal price;
    @NotNull
    private Long dishCategoryId;
    @NotBlank
    private String description;
    @NotBlank
    private String urlImage;
    @NotNull
    private Long restaurantId;
}
