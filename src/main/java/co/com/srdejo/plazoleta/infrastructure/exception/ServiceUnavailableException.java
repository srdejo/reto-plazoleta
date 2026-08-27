package co.com.srdejo.plazoleta.infrastructure.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message,  Throwable cause) {
        super(message, cause);
    }
}
