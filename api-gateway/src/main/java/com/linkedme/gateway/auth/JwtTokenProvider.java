package com.linkedme.gateway.auth;

import com.linkedme.gateway.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Single responsibility: JWT token lifecycle — create, validate, read claims.
 * The gateway is the only service that touches JWT; downstream services
 * receive identity via internal headers (X-User-Id, etc.).
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String CLAIM_USER_ID  = "uid";
    private static final String CLAIM_EMAIL    = "email";
    private static final String CLAIM_NAME     = "name";
    private static final String CLAIM_ROLE     = "role";

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(
                properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String email, String name, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getExpirationMs());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claims(Map.of(
                        CLAIM_USER_ID, userId,
                        CLAIM_EMAIL, email,
                        CLAIM_NAME, name != null ? name : email,
                        CLAIM_ROLE, role != null ? role : "USER"
                ))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Validates signature and expiry. Returns empty if token is invalid.
     */
    public Optional<Claims> parseAndValidate(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (claims.getExpiration().before(new Date())) {
                return Optional.empty();
            }
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Long getUserId(Claims claims) {
        Object uid = claims.get(CLAIM_USER_ID);
        if (uid instanceof Integer i) return i.longValue();
        if (uid instanceof Long l) return l;
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getEmail(Claims claims) {
        return claims.get(CLAIM_EMAIL, String.class);
    }

    public String getName(Claims claims) {
        return claims.get(CLAIM_NAME, String.class);
    }

    public String getRole(Claims claims) {
        String role = claims.get(CLAIM_ROLE, String.class);
        return role != null ? role : "USER";
    }

    public String getJti(Claims claims) {
        return claims.getId();
    }
}
