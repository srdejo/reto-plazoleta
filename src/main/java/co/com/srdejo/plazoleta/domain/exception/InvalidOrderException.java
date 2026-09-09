package co.com.srdejo.plazoleta.domain.exception;

public class InvalidOrderException extends DomainException {

    public InvalidOrderException(ErrorCodesEnum errorCode) {
        super(errorCode);
    }

}
