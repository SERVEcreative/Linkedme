package com.linkedme.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

    /** Base URL of the user-service for login/register delegation. */
    private String userServiceUri = "http://localhost:8081";

    /** Path prefixes that skip JWT authentication. */
    private List<String> publicPaths = new ArrayList<>(List.of(
            "/api/users/register",
            "/api/users/login",
            "/api/users/logout",
            "/actuator",
            "/health"
    ));

    /** Path prefixes for login/register that trigger token issuance. */
    private List<String> authPaths = new ArrayList<>(List.of(
            "/api/users/login",
            "/api/users/register"
    ));
}
