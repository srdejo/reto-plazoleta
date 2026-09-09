package co.com.srdejo.plazoleta.infrastructure.exceptionhandler;

import lombok.Getter;

@Getter
public enum ExceptionResponse {
    NO_DATA_FOUND("No data found for the requested petition"),
    SERVICE_UNAVAILABLE("Service unavailable"),
    INTERNAL_SERVER_ERROR("An unexpected error occurred");

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

}