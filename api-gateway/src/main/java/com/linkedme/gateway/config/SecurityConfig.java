package com.linkedme.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;

/**
 * Spring Security is deliberately permissive here because authentication
 * is handled by our custom filters (AuthenticationFilter, LoginRegisterFilter).
 * This config adds security headers and disables CSRF (stateless JWT API).
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .headers(headers -> headers
                        .frameOptions(frame -> frame
                                .mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY))
                        .contentTypeOptions(contentType -> {})
                        .cache(cache -> {})
                )
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .build();
    }
}
