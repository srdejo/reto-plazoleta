package co.com.srdejo.plazoleta.domain.exception;

public class ActiveOrderExistsException extends DomainException {

    public ActiveOrderExistsException(ErrorCodesEnum errorCode) {
        super(errorCode);
    }

}
