package co.com.srdejo.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantRequestDto {
    @NotBlank
    @Schema(example = "Pizzeria La Bella")
    @Pattern(regexp = ".*[a-zA-Z].*", message = "El nombre del restaurante no puede contener solo números")
    private String name;
    @NotBlank
    @Schema(example = "Dirección de la pizzeria")
    private String address;
    @NotNull
    private long ownerId;
    @NotBlank
    @Size(min = 1, max = 13)
    @Pattern(regexp = "^\\+?[0-9]+$", message = "El teléfono debe contener solo números y opcionalmente el símbolo +")
    private String phoneNumber;
    @NotBlank
    private String urlLogo;
    @NotBlank
    @Digits(fraction = 0, integer = 13)
    private String taxId;
}
