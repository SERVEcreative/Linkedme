package com.linkedme.gateway.filter;

import com.linkedme.gateway.auth.JwtTokenProvider;
import com.linkedme.gateway.auth.TokenBlacklistService;
import com.linkedme.gateway.config.properties.GatewayProperties;
import com.linkedme.gateway.support.FilterOrder;
import com.linkedme.gateway.support.GatewayHeaders;
import com.linkedme.gateway.support.ResponseHelper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Core authentication filter — validates the JWT on every protected request.
 *
 * On success:
 *   - Strips the Authorization header (never forwarded to backends)
 *   - Injects identity headers: X-User-Id, X-User-Email, X-User-Name, X-User-Role
 *
 * On failure:
 *   - Returns 401 with a structured error response
 *
 * Public paths (login, register, health) skip this filter entirely.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService blacklistService;
    private final GatewayProperties gatewayProperties;
    private final ResponseHelper responseHelper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        String token = extractBearerToken(exchange);
        if (token == null) {
            return responseHelper.unauthorized(exchange, "Missing or invalid authorization header");
        }

        Optional<Claims> maybeClaims = jwtTokenProvider.parseAndValidate(token);
        if (maybeClaims.isEmpty()) {
            return responseHelper.unauthorized(exchange, "Invalid or expired token");
        }

        Claims claims = maybeClaims.get();
        Long userId = jwtTokenProvider.getUserId(claims);
        if (userId == null) {
            return responseHelper.unauthorized(exchange, "Token missing user identity");
        }

        String jti = jwtTokenProvider.getJti(claims);
        return blacklistService.isBlacklisted(jti)
                .flatMap(revoked -> {
                    if (revoked) {
                        return responseHelper.unauthorized(exchange, "Token has been revoked");
                    }
                    return forwardWithIdentityHeaders(exchange, chain, claims, userId);
                });
    }

    private Mono<Void> forwardWithIdentityHeaders(ServerWebExchange exchange, GatewayFilterChain chain,
                                                    Claims claims, Long userId) {
        String email = jwtTokenProvider.getEmail(claims);
        String name = jwtTokenProvider.getName(claims);
        String role = jwtTokenProvider.getRole(claims);

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .headers(h -> h.remove(HttpHeaders.AUTHORIZATION))
                .header(GatewayHeaders.USER_ID, String.valueOf(userId))
                .header(GatewayHeaders.USER_EMAIL, safe(email))
                .header(GatewayHeaders.USER_NAME, safe(name))
                .header(GatewayHeaders.USER_ROLE, role)
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private String extractBearerToken(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private boolean isPublic(String path) {
        return gatewayProperties.getPublicPaths().stream().anyMatch(path::startsWith);
    }

    private static String safe(String value) {
        return value != null ? value : "";
    }

    @Override
    public int getOrder() {
        return FilterOrder.AUTHENTICATION;
    }
}
