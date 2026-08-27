package co.com.srdejo.plazoleta.application.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantResponseDto {
    private Long id;
    private String name;
    private String address;
    private long ownerId;
    private String phoneNumber;
    private String urlLogo;
    private String taxId;
}
