package com.linkedme.gateway.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response from user-service after successful login/register.
 * The gateway reads this, then wraps it with a JWT in AuthResponse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthPayload {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String role;
}
