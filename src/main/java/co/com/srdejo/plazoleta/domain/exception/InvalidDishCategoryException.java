package co.com.srdejo.plazoleta.domain.exception;

public class InvalidDishCategoryException extends DomainException {

    public InvalidDishCategoryException(ErrorCodesEnum errorCode) {
        super(errorCode);
    }

}
