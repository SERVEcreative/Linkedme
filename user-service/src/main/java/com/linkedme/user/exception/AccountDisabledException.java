package com.linkedme.user.exception;

/**
 * Thrown when login is attempted for a deactivated user account.
 * Returns 403 Forbidden.
 */
public class AccountDisabledException extends RuntimeException {

    public AccountDisabledException() {
        super("Account is deactivated. Please contact support.");
    }

    public AccountDisabledException(String message) {
        super(message);
    }
}
