package co.com.srdejo.plazoleta.infrastructure.security;

import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.infrastructure.configuration.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurants/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/dishes/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/dishes/**").hasRole("OWNER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((_, response, _) ->
                                SecurityResponseWriter.write(
                                        response,
                                        HttpStatus.UNAUTHORIZED.value(),
                                        ErrorCodesEnum.MISSING_OR_INVALID_TOKEN
                                ))
                        .accessDeniedHandler((_, response, _) ->
                                SecurityResponseWriter.write(
                                        response,
                                        HttpStatus.FORBIDDEN.value(),
                                        ErrorCodesEnum.FORBIDDEN_ROLE
                                ))
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}
