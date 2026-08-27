package co.com.srdejo.plazoleta.infrastructure.exceptionhandler;

import lombok.Getter;

@Getter
public enum ExceptionResponse {
    NO_DATA_FOUND("No data found for the requested petition"),
    SERVICE_UNAVAILABLE("Service unavailable");

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

}