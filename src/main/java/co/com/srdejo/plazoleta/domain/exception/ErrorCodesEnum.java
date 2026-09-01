package co.com.srdejo.plazoleta.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodesEnum {

    INVALID_OWNER_ID("ERR01", "El identificador del propietario no es valido"),
    INVALID_DISH_CATEGORY_ID("ERR02", "La categoria de plato indicada no existe"),
    OWNER_NOT_AUTHORIZED("AUTH1", "No tienes permisos para crear platos en este restaurante"),
    SERVICE_UNAVAILABLE("HTTP1", "El servicio %s no responde"),
    FORBIDDEN_ROLE("AUTH2", "Tu rol no tiene permisos para realizar esta accion"),
    MISSING_OR_INVALID_TOKEN("AUTH3", "Token de autenticacion ausente o invalido");

    private final String code;
    private final String description;

}
