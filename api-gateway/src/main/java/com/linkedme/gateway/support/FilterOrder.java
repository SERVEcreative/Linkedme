package com.linkedme.gateway.support;

/**
 * Centralized filter execution order. Lower value = executes first.
 *
 * Execution flow:
 *   RequestId → Logging → RateLimit → LoginRegister → Logout → Authentication → [route to service]
 *
 * Gaps of 50 between filters allow inserting new filters without renumbering.
 */
public final class FilterOrder {

    private FilterOrder() {}

    public static final int REQUEST_ID       = -500;
    public static final int REQUEST_LOGGING  = -450;
    public static final int RATE_LIMIT       = -350;
    public static final int LOGIN_REGISTER   = -250;
    public static final int LOGOUT           = -200;
    public static final int AUTHENTICATION   = -150;
}
