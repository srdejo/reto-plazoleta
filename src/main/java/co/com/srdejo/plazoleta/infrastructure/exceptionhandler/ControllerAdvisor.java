package co.com.srdejo.plazoleta.infrastructure.exceptionhandler;

import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidDishCategoryException;
import co.com.srdejo.plazoleta.domain.exception.InvalidOwnerException;
import co.com.srdejo.plazoleta.domain.exception.UnauthorizedException;
import co.com.srdejo.plazoleta.infrastructure.exception.NoDataFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerAdvisor {

    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";


    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoDataFoundException(
            NoDataFoundException ignoredNoDataFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.NO_DATA_FOUND.getMessage()));
    }

    @ExceptionHandler(InvalidOwnerException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(InvalidOwnerException invalidOwnerException) {

        return getMapResponseEntity(invalidOwnerException.getError());
    }

    @ExceptionHandler(InvalidDishCategoryException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(InvalidDishCategoryException invalidDishCategoryException) {

        return getMapResponseEntity(invalidDishCategoryException.getError());
    }

    @ExceptionHandler(feign.RetryableException.class)
    public ResponseEntity<Map<String, Object>> handleFeignRetryable(feign.RetryableException ex) {
        String serviceName = ex.getMessage();
        String message = String.format(ErrorCodesEnum.SERVICE_UNAVAILABLE.getDescription(), serviceName);

        log.error(ex.getMessage(), ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(CODE, ErrorCodesEnum.SERVICE_UNAVAILABLE.getCode());
        body.put(MESSAGE, message);
        body.put(TIMESTAMP, Instant.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(UnauthorizedException exception) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(CODE, exception.getError().getCode());
        body.put(MESSAGE, exception.getError().getDescription());
        body.put(TIMESTAMP, Instant.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(MESSAGE, message);
        body.put(TIMESTAMP, Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @NonNull
    private ResponseEntity<Map<String, Object>> getMapResponseEntity(ErrorCodesEnum error) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(CODE, error.getCode());
        body.put(MESSAGE, error.getDescription());
        body.put(TIMESTAMP, Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

}