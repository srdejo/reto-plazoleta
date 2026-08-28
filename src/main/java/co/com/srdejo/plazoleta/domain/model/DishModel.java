package co.com.srdejo.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishModel {
    private Long id;
    private String name;
    private BigDecimal price;
    private Long dishCategoryId;
    private Long restaurantId;
    private String description;
    private String urlImage;
    private Boolean active;
}
