package co.com.srdejo.plazoleta.domain.exception;

import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {

    private final ErrorCodesEnum error;

    protected DomainException(ErrorCodesEnum errorCode) {
        super(errorCode.getDescription());
        this.error = errorCode;
    }

}
