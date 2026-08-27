package co.com.srdejo.plazoleta.infrastructure.exceptionhandler;

import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOwnerException;
import co.com.srdejo.plazoleta.infrastructure.exception.NoDataFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(CODE, invalidOwnerException.getError().getCode());
        body.put(MESSAGE, invalidOwnerException.getError().getDescription());
        body.put(TIMESTAMP, Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
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

}