package co.com.srdejo.plazoleta.domain.exception;

public class InvalidOwnerException extends DomainException {

    public InvalidOwnerException(ErrorCodesEnum errorCode) {
        super(errorCode);
    }

}
