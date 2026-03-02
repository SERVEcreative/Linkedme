package com.linkedme.gateway.auth;

import com.linkedme.gateway.config.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Redis-backed token blacklist for logout support.
 * Stores the token's JTI (unique id) with a TTL matching the token's expiration.
 * After TTL, the entry auto-expires (no need to clean up — the token itself
 * would also be expired by then).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String KEY_PREFIX = "jwt:blacklist:";

    private final ReactiveStringRedisTemplate redis;
    private final JwtProperties jwtProperties;

    public Mono<Boolean> isBlacklisted(String jti) {
        if (jti == null) {
            return Mono.just(false);
        }
        return redis.hasKey(KEY_PREFIX + jti)
                .onErrorResume(e -> {
                    log.warn("Redis unavailable for blacklist check, allowing token: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    public Mono<Boolean> blacklist(String jti) {
        if (jti == null) {
            return Mono.just(true);
        }
        Duration ttl = Duration.ofMillis(jwtProperties.getExpirationMs());
        return redis.opsForValue().set(KEY_PREFIX + jti, "revoked", ttl)
                .onErrorResume(e -> {
                    log.error("Failed to blacklist token {}: {}", jti, e.getMessage());
                    return Mono.just(false);
                });
    }
}
