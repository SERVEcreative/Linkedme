package com.linkedme.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedme.gateway.support.GatewayHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.time.Instant;

/**
 * Catches unhandled exceptions from any filter or route and returns
 * a consistent JSON error response. Without this, Spring defaults
 * to an HTML error page.
 */
@Slf4j
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = resolveStatus(ex);
        String message = resolveMessage(ex, status);

        log.error("Gateway error [{}] {}: {}", status.value(),
                exchange.getRequest().getURI().getPath(), ex.getMessage());

        String requestId = exchange.getRequest().getHeaders().getFirst(GatewayHeaders.REQUEST_ID);
        ErrorResponse body = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getURI().getPath())
                .requestId(requestId)
                .timestamp(Instant.now().toString())
                .build();

        return writeResponse(exchange.getResponse(), status, body);
    }

    private HttpStatus resolveStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException rse) {
            return HttpStatus.resolve(rse.getStatusCode().value());
        }
        if (ex instanceof ConnectException) {
            return HttpStatus.BAD_GATEWAY;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String resolveMessage(Throwable ex, HttpStatus status) {
        if (status == HttpStatus.BAD_GATEWAY) {
            return "Downstream service unavailable";
        }
        if (status == HttpStatus.NOT_FOUND) {
            return "Route not found";
        }
        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            return "Service temporarily unavailable";
        }
        return ex.getMessage() != null ? ex.getMessage() : "Unexpected error";
    }

    private Mono<Void> writeResponse(ServerHttpResponse response, HttpStatus status, ErrorResponse body) {
        if (response.isCommitted()) {
            return Mono.empty();
        }
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
