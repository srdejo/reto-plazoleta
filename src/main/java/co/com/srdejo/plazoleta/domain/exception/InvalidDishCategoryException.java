package co.com.srdejo.plazoleta.domain.exception;

import lombok.Getter;

@Getter
public class InvalidDishCategoryException extends RuntimeException {

    private final ErrorCodesEnum error;

    public InvalidDishCategoryException(ErrorCodesEnum errorCode) {
        super(errorCode.getDescription());
        this.error = errorCode;
    }

}
