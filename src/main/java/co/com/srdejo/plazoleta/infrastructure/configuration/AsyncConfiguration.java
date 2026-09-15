package co.com.srdejo.plazoleta.infrastructure.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.setTaskDecorator(requestContextPropagatingDecorator());
        executor.initialize();
        return executor;
    }

    private TaskDecorator requestContextPropagatingDecorator() {
        return runnable -> {
            String authorizationHeader = currentAuthorizationHeader();
            return () -> {
                try {
                    if (authorizationHeader != null) {
                        AsyncAuthorizationContext.set(authorizationHeader);
                    }
                    runnable.run();
                } finally {
                    AsyncAuthorizationContext.clear();
                }
            };
        };
    }

    private String currentAuthorizationHeader() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request.getHeader("Authorization");
    }
}
