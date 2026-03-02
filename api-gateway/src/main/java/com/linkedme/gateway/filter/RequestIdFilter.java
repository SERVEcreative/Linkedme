package com.linkedme.gateway.filter;

import com.linkedme.gateway.support.FilterOrder;
import com.linkedme.gateway.support.GatewayHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Assigns a unique request ID to every incoming request for distributed tracing.
 * If the client already provides X-Request-Id (e.g., from a load balancer), it is kept.
 * The ID is propagated to downstream services and included in error responses.
 *
 * This is the first filter in the chain — every subsequent filter can read the ID.
 */
@Component
public class RequestIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String existing = exchange.getRequest().getHeaders().getFirst(GatewayHeaders.REQUEST_ID);
        String requestId = (existing != null && !existing.isBlank()) ? existing : UUID.randomUUID().toString();

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header(GatewayHeaders.REQUEST_ID, requestId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutated)
                .build();

        mutatedExchange.getResponse().getHeaders().add(GatewayHeaders.REQUEST_ID, requestId);

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return FilterOrder.REQUEST_ID;
    }
}
