package co.com.srdejo.plazoleta.infrastructure.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignAuthorizationInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        String authorizationHeader = resolveAuthorizationHeader();
        if (authorizationHeader != null) {
            template.header(AUTHORIZATION_HEADER, authorizationHeader);
        }
    }

    private String resolveAuthorizationHeader() {
        HttpServletRequest currentRequest = currentRequest();
        if (currentRequest != null) {
            return currentRequest.getHeader(AUTHORIZATION_HEADER);
        }
        return AsyncAuthorizationContext.get();
    }

    private HttpServletRequest currentRequest() {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}
