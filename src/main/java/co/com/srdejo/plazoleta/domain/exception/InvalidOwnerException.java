package co.com.srdejo.plazoleta.domain.exception;

import lombok.Getter;

@Getter
public class InvalidOwnerException extends RuntimeException {

    private final ErrorCodesEnum error;

    public InvalidOwnerException(ErrorCodesEnum errorCode) {
        super(errorCode.getDescription());
        this.error = errorCode;
    }

}
