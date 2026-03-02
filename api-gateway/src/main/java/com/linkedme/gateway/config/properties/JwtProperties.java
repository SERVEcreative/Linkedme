package com.linkedme.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gateway.jwt")
public class JwtProperties {

    private String secret = "your-secret-key-change-this-in-production-minimum-256-bits";

    /** Token time-to-live in milliseconds. Default: 24 hours. */
    private long expirationMs = 86_400_000;
}
