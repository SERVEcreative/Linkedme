package com.linkedme.gateway.filter;

import com.linkedme.gateway.auth.JwtTokenProvider;
import com.linkedme.gateway.auth.dto.AuthPayload;
import com.linkedme.gateway.auth.dto.AuthResponse;
import com.linkedme.gateway.config.properties.GatewayProperties;
import com.linkedme.gateway.config.properties.JwtProperties;
import com.linkedme.gateway.support.FilterOrder;
import com.linkedme.gateway.support.ResponseHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Intercepts login and register requests, delegates credential validation
 * to user-service, then wraps the response with a JWT.
 *
 * Flow:
 *   1. Client POSTs to /api/users/login or /api/users/register
 *   2. This filter reads the body and forwards it to user-service
 *   3. If user-service returns 2xx, the gateway issues a JWT
 *   4. Client receives AuthResponse with token + user info
 *   5. If user-service returns an error, it's passed through as-is
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginRegisterFilter implements GlobalFilter, Ordered {

    private final WebClient.Builder webClientBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final GatewayProperties gatewayProperties;
    private final ResponseHelper responseHelper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (!HttpMethod.POST.equals(exchange.getRequest().getMethod())
                || !gatewayProperties.getAuthPaths().contains(path)) {
            return chain.filter(exchange);
        }

        String userServiceUri = buildDownstreamUri(path);
        log.info("Auth request intercepted: {} → {}", path, userServiceUri);

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    return bytes;
                })
                .defaultIfEmpty(new byte[0])
                .flatMap(body -> delegateToUserService(exchange, userServiceUri, body));
    }

    private Mono<Void> delegateToUserService(ServerWebExchange exchange, String uri, byte[] body) {
        WebClient client = webClientBuilder.build();

        return client.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromDataBuffers(
                        Mono.just(exchange.getResponse().bufferFactory().wrap(body))))
                .exchangeToMono(downstream -> {
                    if (downstream.statusCode().is2xxSuccessful()) {
                        return downstream.bodyToMono(AuthPayload.class)
                                .flatMap(payload -> issueTokenAndRespond(exchange, payload));
                    }

                    HttpStatus status = HttpStatus.resolve(downstream.statusCode().value());
                    return downstream.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(responseBody -> responseHelper.passthrough(
                                    exchange.getResponse(),
                                    status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR,
                                    responseBody));
                })
                .onErrorResume(e -> {
                    log.error("user-service unreachable at {}: {}", uri, e.getMessage());
                    return responseHelper.error(exchange, HttpStatus.BAD_GATEWAY,
                            "Authentication service unavailable");
                });
    }

    private Mono<Void> issueTokenAndRespond(ServerWebExchange exchange, AuthPayload payload) {
        String role = payload.getRole() != null ? payload.getRole() : "USER";
        String token = jwtTokenProvider.generateToken(
                payload.getUserId(),
                payload.getEmail(),
                payload.getFullName(),
                role);

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpirationMs() / 1000)
                .userId(payload.getUserId())
                .email(payload.getEmail())
                .firstName(payload.getFirstName())
                .lastName(payload.getLastName())
                .fullName(payload.getFullName())
                .build();

        return responseHelper.ok(exchange, response);
    }

    private String buildDownstreamUri(String gatewayPath) {
        String servicePath = gatewayPath.replaceFirst("^/api", "");
        return gatewayProperties.getUserServiceUri().replaceAll("/$", "") + servicePath;
    }

    @Override
    public int getOrder() {
        return FilterOrder.LOGIN_REGISTER;
    }
}
