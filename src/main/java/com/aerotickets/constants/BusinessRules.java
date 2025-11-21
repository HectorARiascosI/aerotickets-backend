package com.aerotickets.constants;

import java.math.BigDecimal;

/**
 * Business rules and default values for the application.
 * Contains domain-specific constants and configurations.
 */
public final class BusinessRules {

    private BusinessRules() {
    }

    public static final class Flight {
        private Flight() {}
        public static final int DEFAULT_TOTAL_SEATS = 180;
        public static final int DEFAULT_DURATION_HOURS = 2;
        public static final BigDecimal DEFAULT_PRICE = BigDecimal.ZERO;
        public static final String DEFAULT_AIRLINE_NAME = "Aerotickets";
        public static final String OFFSET_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX";
    }

    public static final class Auth {
        private Auth() {}
        public static final int TEMP_TOKEN_MINUTES = 10;
        public static final int MIN_JWT_SECRET_LENGTH = 32;
    }

    public static final class Email {
        private Email() {}
        public static final String SUBJECT_PASSWORD_RESET = "Password Recovery - Aerotickets";
        public static final String TEMPLATE_PASSWORD_RESET_TEXT =
                "Hello,%n%n" +
                "We have received a request to reset the password for your Aerotickets account.%n%n" +
                "To create a new password, use this link:%n%n%s%n%n" +
                "If you did not request this change, you can ignore this message.%n%n" +
                "Best regards,%n" +
                "Aerotickets Team";
    }
}
