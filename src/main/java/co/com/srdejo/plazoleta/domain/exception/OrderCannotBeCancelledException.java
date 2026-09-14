package co.com.srdejo.plazoleta.domain.exception;

public class OrderCannotBeCancelledException extends DomainException {
    public OrderCannotBeCancelledException(ErrorCodesEnum errorCode) {
        super(errorCode);
    }
}
