package co.com.srdejo.plazoleta.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DishResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private long dishCategoryId;
    private String description;
    private String urlImage;
}
