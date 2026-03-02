package com.linkedme.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** User info returned on login/register. Gateway issues the JWT. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResult {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    @Builder.Default
    private String role = "USER";
}
