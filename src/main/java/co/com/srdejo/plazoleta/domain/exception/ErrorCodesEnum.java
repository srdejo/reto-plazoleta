package co.com.srdejo.plazoleta.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodesEnum {

    INVALID_OWNER_ID("ERR01", "El identificador del propietario no es valido"),
    INVALID_DISH_CATEGORY_ID("ERR02", "La categoria de plato indicada no existe"),
    ACTIVE_ORDER_EXISTS("ERR03", "El cliente ya tiene una orden activa"),
    MISSING_ORDER_DATA("ERR04", "El pedido debe especificar el restaurante, los platos y la cantidad de cada uno"),
    DISHES_DIFFERENT_RESTAURANT("ERR05", "Todos los platos del pedido deben pertenecer al mismo restaurante"),
    OWNER_NOT_AUTHORIZED("AUTH1", "No tienes permisos para crear platos en este restaurante"),
    SERVICE_UNAVAILABLE("HTTP1", "El servicio %s no responde"),
    FORBIDDEN_ROLE("AUTH2", "Tu rol no tiene permisos para realizar esta accion"),
    MISSING_OR_INVALID_TOKEN("AUTH3", "Token de autenticacion ausente o invalido"),
    EMPLOYEE_NOT_FOUND("ERR06", "El empleado autenticado no existe o no tiene un restaurante asignado"),
    NOT_YET_READY_EXCEPTION("ERR07", "El pedido aun no esta listo para marcarse como entregado"),
    INVALID_PIN_EXCEPTION("ERR08", "El pin es incorrecto");

    private final String code;
    private final String description;

}
