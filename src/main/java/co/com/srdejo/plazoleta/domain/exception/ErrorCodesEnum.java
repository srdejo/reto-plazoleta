package co.com.srdejo.plazoleta.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodesEnum {

    INVALID_OWNER_ID("ERR01", "El identificador del propietario no es valido"),
    OWNER_NOT_AUTHORIZED("AUTH1", "No tienes permisos para crear platos en este restaurante"),
    SERVICE_UNAVAILABLE("HTTP1", "El servicio %s no responde");

    private final String code;
    private final String description;

}
