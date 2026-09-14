package co.com.srdejo.plazoleta.domain.exception;

public class InvalidOrderPinException extends DomainException {
    public InvalidOrderPinException(ErrorCodesEnum errorCode) {
        super(errorCode);
    }
}
