package com.linkedme.gateway.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedme.gateway.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class ResponseHelper {

    private final ObjectMapper objectMapper;

    public ResponseHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Mono<Void> writeJson(ServerHttpResponse response, HttpStatus status, Object body) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<Void> error(ServerWebExchange exchange, HttpStatus status, String message) {
        String path = exchange.getRequest().getURI().getPath();
        String requestId = exchange.getRequest().getHeaders().getFirst(GatewayHeaders.REQUEST_ID);

        ErrorResponse body = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .requestId(requestId)
                .timestamp(Instant.now().toString())
                .build();

        return writeJson(exchange.getResponse(), status, body);
    }

    public Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        return error(exchange, HttpStatus.UNAUTHORIZED, message);
    }

    public Mono<Void> ok(ServerWebExchange exchange, Object body) {
        return writeJson(exchange.getResponse(), HttpStatus.OK, body);
    }

    public Mono<Void> passthrough(ServerHttpResponse response, HttpStatus status, String rawBody) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = (rawBody != null ? rawBody : "").getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
