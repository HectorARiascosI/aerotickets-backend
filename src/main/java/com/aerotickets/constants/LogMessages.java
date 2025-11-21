package com.aerotickets.constants;

/**
 * Log messages for application logging.
 * All log messages should be defined here for consistency.
 */
public final class LogMessages {

    private LogMessages() {
    }

    public static final class Startup {
        private Startup() {}
        public static final String BANNER_HEADER = "\n==============================================";
        public static final String BANNER_FOOTER = "==============================================\n";
        public static final String BANNER_TITLE = " Aerotickets Backend started";
        public static final String BANNER_ENV_PREFIX = " Environment: ";
        public static final String BANNER_PORT_PREFIX = " Port       : ";
        public static final String BANNER_DB_URL_PREFIX = " DB URL     : ";
        public static final String BANNER_LOGS_LINE = " Logs       : logs/aerotickets.log";
        public static final String ENDPOINTS_HEADER = "========= REGISTERED ENDPOINTS =========";
        public static final String ENDPOINTS_FOOTER = "=========================================";
        public static final String ENDPOINTS_ERROR_PREFIX = "Could not retrieve endpoint list: ";
    }

    public static final class Email {
        private Email() {}
        public static final String SENDGRID_MISSING_API_KEY = "SENDGRID_API_KEY not configured. Cannot send recovery email.";
        public static final String PASSWORD_RESET_SENT = "Password recovery email sent to {}";
        public static final String SENDGRID_ERROR = "SendGrid error (status {}): {}";
        public static final String PASSWORD_RESET_ERROR = "Error sending recovery email to {}: {}";
    }

    public static final class Security {
        private Security() {}
        public static final String UNAUTHORIZED_ACCESS_ATTEMPT = "Unauthorized access attempt to: {}";
        public static final String JWT_VALIDATION_FAILED = "JWT validation failed: {}";
    }
}
