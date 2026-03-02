package com.linkedme.gateway.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response sent to the client after successful authentication.
 * Contains the JWT along with basic user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
}
