package co.com.srdejo.plazoleta.infrastructure.security;

import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

final class SecurityResponseWriter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    private SecurityResponseWriter() {
    }

    static void write(HttpServletResponse response, int status, ErrorCodesEnum error) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", error.getCode());
        body.put("message", error.getDescription());
        body.put("timestamp", Instant.now());

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(body));
    }
}
