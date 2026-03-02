package com.linkedme.user.exception;

/**
 * Thrown when login fails due to invalid email or password.
 * Returns 401 Unauthorized. Use a generic message to avoid user enumeration.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
