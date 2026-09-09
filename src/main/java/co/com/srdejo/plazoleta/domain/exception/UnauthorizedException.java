package co.com.srdejo.plazoleta.domain.exception;

public class UnauthorizedException extends DomainException {

    public UnauthorizedException(ErrorCodesEnum errorCode) {
        super(errorCode);
    }

}
