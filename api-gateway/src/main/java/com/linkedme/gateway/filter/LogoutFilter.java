package com.linkedme.gateway.filter;

import com.linkedme.gateway.auth.JwtTokenProvider;
import com.linkedme.gateway.auth.TokenBlacklistService;
import com.linkedme.gateway.support.FilterOrder;
import com.linkedme.gateway.support.ResponseHelper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

/**
 * Handles POST /api/users/logout by blacklisting the token's JTI in Redis.
 * Subsequent requests with the same token will be rejected by AuthenticationFilter.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutFilter implements GlobalFilter, Ordered {

    private static final String PATH_LOGOUT = "/api/users/logout";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService blacklistService;
    private final ResponseHelper responseHelper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!isLogoutRequest(exchange)) {
            return chain.filter(exchange);
        }

        String token = extractBearerToken(exchange);
        if (token == null) {
            return responseHelper.unauthorized(exchange, "Missing or invalid token");
        }

        Optional<Claims> claims = jwtTokenProvider.parseAndValidate(token);
        if (claims.isEmpty()) {
            return responseHelper.unauthorized(exchange, "Invalid or expired token");
        }

        String jti = jwtTokenProvider.getJti(claims.get());
        log.info("Logout: blacklisting token jti={}", jti);

        return blacklistService.blacklist(jti)
                .then(responseHelper.ok(exchange, Map.of("message", "Successfully logged out")));
    }

    private boolean isLogoutRequest(ServerWebExchange exchange) {
        return HttpMethod.POST.equals(exchange.getRequest().getMethod())
                && PATH_LOGOUT.equals(exchange.getRequest().getURI().getPath());
    }

    private String extractBearerToken(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    @Override
    public int getOrder() {
        return FilterOrder.LOGOUT;
    }
}
