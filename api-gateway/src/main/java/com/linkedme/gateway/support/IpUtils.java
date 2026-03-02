package com.linkedme.gateway.support;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetSocketAddress;

public final class IpUtils {

    private IpUtils() {}

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_X_REAL_IP = "X-Real-IP";
    private static final String UNKNOWN = "unknown";

    /**
     * Extracts the real client IP, respecting proxy headers.
     * Checks X-Forwarded-For first (standard for load balancers), then X-Real-IP,
     * then falls back to the direct remote address.
     */
    public static String getClientIp(ServerHttpRequest request) {
        String xff = request.getHeaders().getFirst(HEADER_X_FORWARDED_FOR);
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }

        String realIp = request.getHeaders().getFirst(HEADER_X_REAL_IP);
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            return remoteAddress.getAddress().getHostAddress();
        }

        return UNKNOWN;
    }
}
