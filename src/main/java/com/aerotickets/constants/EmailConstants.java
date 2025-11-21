package com.aerotickets.constants;

/**
 * @deprecated Use {@link LogMessages.Email}, {@link BusinessRules.Email}, and {@link ConfigKeys.Email} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class EmailConstants {

    private EmailConstants() {
    }

    public static final String LOG_SENDGRID_MISSING_API_KEY = LogMessages.Email.SENDGRID_MISSING_API_KEY;

    public static final String SUBJECT_PASSWORD_RESET = BusinessRules.Email.SUBJECT_PASSWORD_RESET;
    public static final String SENDER_NAME = ConfigKeys.Email.SENDER_NAME;

    public static final String TEMPLATE_PASSWORD_RESET_TEXT = BusinessRules.Email.TEMPLATE_PASSWORD_RESET_TEXT;

    public static final String LOG_PASSWORD_RESET_SENT = LogMessages.Email.PASSWORD_RESET_SENT;
    public static final String LOG_SENDGRID_ERROR = LogMessages.Email.SENDGRID_ERROR;
    public static final String LOG_PASSWORD_RESET_ERROR = LogMessages.Email.PASSWORD_RESET_ERROR;
}