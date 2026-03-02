package com.linkedme.gateway.filter;

import com.linkedme.gateway.config.properties.RateLimitProperties;
import com.linkedme.gateway.support.FilterOrder;
import com.linkedme.gateway.support.GatewayHeaders;
import com.linkedme.gateway.support.IpUtils;
import com.linkedme.gateway.support.ResponseHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Fixed-window rate limiter backed by Redis.
 *
 * For authenticated requests: limits by userId.
 * For anonymous requests: limits by client IP.
 *
 * Adds standard rate-limit headers to every response:
 *   X-RateLimit-Limit: max requests per window
 *   X-RateLimit-Remaining: remaining requests in current window
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final String KEY_PREFIX = "rl:";

    private final RateLimitProperties properties;
    private final ReactiveStringRedisTemplate redis;
    private final ResponseHelper responseHelper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }

        String userId = exchange.getRequest().getHeaders().getFirst(GatewayHeaders.USER_ID);
        boolean authenticated = userId != null && !userId.isBlank();

        String identifier = authenticated ? "u:" + userId : "ip:" + IpUtils.getClientIp(exchange.getRequest());
        int limit = authenticated ? properties.getRequestsPerWindow() : properties.getAnonymousRequestsPerWindow();

        long windowId = System.currentTimeMillis() / (properties.getWindowSeconds() * 1000L);
        String key = KEY_PREFIX + identifier + ":" + windowId;

        return redis.opsForValue().increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        return redis.expire(key, Duration.ofSeconds(properties.getWindowSeconds()))
                                .thenReturn(count);
                    }
                    return Mono.just(count);
                })
                .flatMap(count -> {
                    long remaining = Math.max(0, limit - count);
                    exchange.getResponse().getHeaders().set(GatewayHeaders.RATE_LIMIT_LIMIT, String.valueOf(limit));
                    exchange.getResponse().getHeaders().set(GatewayHeaders.RATE_LIMIT_REMAINING, String.valueOf(remaining));

                    if (count > limit) {
                        log.warn("Rate limit exceeded for {}: {}/{}", identifier, count, limit);
                        return responseHelper.error(exchange, HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
                    }
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    log.warn("Redis unavailable for rate limiting, allowing request: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return FilterOrder.RATE_LIMIT;
    }
}
