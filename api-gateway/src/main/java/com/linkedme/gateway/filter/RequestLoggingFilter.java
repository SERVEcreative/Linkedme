package com.linkedme.gateway.filter;

import com.linkedme.gateway.support.FilterOrder;
import com.linkedme.gateway.support.GatewayHeaders;
import com.linkedme.gateway.support.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Logs every request with method, path, status, latency, and client IP.
 * Format mirrors access logs used by LinkedIn's frontdoor proxy.
 *
 * Example output:
 *   GET /api/profiles/me → 200 (23ms) [ip=10.0.0.1, rid=abc-123]
 */
@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        return chain.filter(exchange)
                .doFinally(signal -> {
                    long duration = System.currentTimeMillis() - startTime;
                    HttpMethod method = request.getMethod();
                    String path = request.getURI().getPath();
                    int status = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value() : 0;
                    String clientIp = IpUtils.getClientIp(request);
                    String requestId = request.getHeaders().getFirst(GatewayHeaders.REQUEST_ID);

                    if (status >= 500) {
                        log.error("{} {} → {} ({}ms) [ip={}, rid={}]",
                                method, path, status, duration, clientIp, requestId);
                    } else if (status >= 400) {
                        log.warn("{} {} → {} ({}ms) [ip={}, rid={}]",
                                method, path, status, duration, clientIp, requestId);
                    } else {
                        log.info("{} {} → {} ({}ms) [ip={}, rid={}]",
                                method, path, status, duration, clientIp, requestId);
                    }
                });
    }

    @Override
    public int getOrder() {
        return FilterOrder.REQUEST_LOGGING;
    }
}
