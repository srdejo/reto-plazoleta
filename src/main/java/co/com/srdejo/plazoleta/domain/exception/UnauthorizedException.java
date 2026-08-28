package co.com.srdejo.plazoleta.domain.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final ErrorCodesEnum error;

    public UnauthorizedException(ErrorCodesEnum errorCode) {
        super(errorCode.getDescription());
        this.error = errorCode;
    }

}
