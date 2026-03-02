package com.linkedme.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gateway.rate-limit")
public class RateLimitProperties {

    /** Whether rate limiting is enabled. */
    private boolean enabled = true;

    /** Max requests per window for authenticated users. */
    private int requestsPerWindow = 100;

    /** Max requests per window for anonymous users (by IP). */
    private int anonymousRequestsPerWindow = 30;

    /** Window size in seconds. */
    private int windowSeconds = 60;
}
