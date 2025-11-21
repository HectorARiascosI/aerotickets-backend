package com.aerotickets.constants;

/**
 * Success messages for API responses.
 * All user-facing success messages should be defined here.
 */
public final class SuccessMessages {

    private SuccessMessages() {
    }

    public static final class Auth {
        private Auth() {}
        public static final String USER_REGISTERED = "User registered successfully";
        public static final String PASSWORD_RESET_SENT = "If the email is registered, we have sent you a link to reset your password.";
        public static final String PASSWORD_UPDATED = "Your password has been updated successfully. You can now log in.";
    }
}
