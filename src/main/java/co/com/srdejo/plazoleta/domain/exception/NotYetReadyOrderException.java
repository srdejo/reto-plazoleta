package co.com.srdejo.plazoleta.domain.exception;

public class NotYetReadyOrderException extends DomainException {
    public NotYetReadyOrderException(ErrorCodesEnum errorCode) {
        super(errorCode);
    }
}
