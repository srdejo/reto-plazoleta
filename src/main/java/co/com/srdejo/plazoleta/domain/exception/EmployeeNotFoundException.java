package co.com.srdejo.plazoleta.domain.exception;

public class EmployeeNotFoundException extends DomainException {

    public EmployeeNotFoundException(ErrorCodesEnum errorCode) {
        super(errorCode);
    }

}
