package co.com.srdejo.plazoleta.infrastructure.exceptionhandler;

import co.com.srdejo.plazoleta.domain.exception.DomainException;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.infrastructure.exception.NoDataFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerAdvisor {

    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";

    private static final Map<ErrorCodesEnum, HttpStatus> STATUS_BY_ERROR_CODE = Map.of(
            ErrorCodesEnum.INVALID_OWNER_ID, HttpStatus.BAD_REQUEST,
            ErrorCodesEnum.INVALID_DISH_CATEGORY_ID, HttpStatus.BAD_REQUEST,
            ErrorCodesEnum.OWNER_NOT_AUTHORIZED, HttpStatus.FORBIDDEN,
            ErrorCodesEnum.ACTIVE_ORDER_EXISTS, HttpStatus.CONFLICT,
            ErrorCodesEnum.MISSING_ORDER_DATA, HttpStatus.BAD_REQUEST,
            ErrorCodesEnum.DISHES_DIFFERENT_RESTAURANT, HttpStatus.BAD_REQUEST
    );

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoDataFoundException(
            NoDataFoundException ignoredNoDataFoundException) {
        return buildResponse(HttpStatus.NOT_FOUND, null, ExceptionResponse.NO_DATA_FOUND.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(DomainException exception) {
        return buildResponse(exception.getError());
    }

    @ExceptionHandler(feign.RetryableException.class)
    public ResponseEntity<Map<String, Object>> handleFeignRetryable(feign.RetryableException exception) {
        log.error(exception.getMessage(), exception);

        String message = String.format(ErrorCodesEnum.SERVICE_UNAVAILABLE.getDescription(), exception.getMessage());
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodesEnum.SERVICE_UNAVAILABLE.getCode(), message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, null, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(Exception exception) {
        log.error(exception.getMessage(), exception);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, ExceptionResponse.INTERNAL_SERVER_ERROR.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(ErrorCodesEnum error) {
        HttpStatus status = STATUS_BY_ERROR_CODE.getOrDefault(error, HttpStatus.INTERNAL_SERVER_ERROR);
        return buildResponse(status, error.getCode(), error.getDescription());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (code != null) {
            body.put(CODE, code);
        }
        body.put(MESSAGE, message);
        body.put(TIMESTAMP, Instant.now());

        return ResponseEntity.status(status).body(body);
    }

}
