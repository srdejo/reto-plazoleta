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
        HttpServletRequest currentRequest = currentRequest();
        if (currentRequest == null) {
            return;
        }

        String authorizationHeader = currentRequest.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader != null) {
            template.header(AUTHORIZATION_HEADER, authorizationHeader);
        }
    }

    private HttpServletRequest currentRequest() {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}
