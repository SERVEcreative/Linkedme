package com.linkedme.gateway.support;

/**
 * Internal headers injected by the gateway into downstream requests.
 * Backend services read these instead of dealing with JWT directly.
 * Modeled after LinkedIn's frontdoor proxy pattern.
 */
public final class GatewayHeaders {

    private GatewayHeaders() {}

    public static final String USER_ID       = "X-User-Id";
    public static final String USER_EMAIL    = "X-User-Email";
    public static final String USER_NAME     = "X-User-Name";
    public static final String USER_ROLE     = "X-User-Role";
    public static final String REQUEST_ID    = "X-Request-Id";
    public static final String REQUEST_START = "X-Request-Start";

    public static final String RATE_LIMIT_LIMIT     = "X-RateLimit-Limit";
    public static final String RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
}
