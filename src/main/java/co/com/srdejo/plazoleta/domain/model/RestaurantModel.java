package co.com.srdejo.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantModel {
    private Long id;
    private String name;
    private String address;
    private long ownerId;
    private String phoneNumber;
    private String urlLogo;
    private String taxId;
}
