package co.com.srdejo.plazoleta.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DishCategoryRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
}
