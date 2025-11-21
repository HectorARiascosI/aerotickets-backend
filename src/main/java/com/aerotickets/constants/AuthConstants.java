package com.aerotickets.constants;

/**
 * @deprecated Use {@link ApiPaths.Auth}, {@link ErrorMessages.Auth}, {@link SuccessMessages.Auth},
 * {@link ConfigKeys.Cors}, and {@link BusinessRules.Auth} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class AuthConstants {

    private AuthConstants() {
    }

    public static final String BASE_PATH = ApiPaths.Auth.BASE;
    public static final String REGISTER_PATH = ApiPaths.Auth.REGISTER;
    public static final String LOGIN_PATH = ApiPaths.Auth.LOGIN;
    public static final String FORGOT_PASSWORD_PATH = ApiPaths.Auth.FORGOT_PASSWORD;
    public static final String RESET_PASSWORD_PATH = ApiPaths.Auth.RESET_PASSWORD;

    public static final String CORS_ORIGIN_LOCAL = ConfigKeys.Cors.ORIGIN_LOCAL;
    public static final String CORS_ORIGIN_VERCEL_MAIN = ConfigKeys.Cors.ORIGIN_VERCEL_MAIN;
    public static final String CORS_ORIGIN_VERCEL_ENV = ConfigKeys.Cors.ORIGIN_VERCEL_ENV;

    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_TOKEN = "token";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_NEW_PASSWORD = "newPassword";
    public static final String FIELD_MESSAGE = "message";

    public static final String MSG_EMAIL_ALREADY_REGISTERED = ErrorMessages.Auth.EMAIL_ALREADY_REGISTERED;
    public static final String MSG_USER_REGISTERED_SUCCESS = SuccessMessages.Auth.USER_REGISTERED;
    public static final String MSG_USER_NOT_FOUND = ErrorMessages.Auth.USER_NOT_FOUND;

    public static final String MSG_EMAIL_REQUIRED = ErrorMessages.Auth.EMAIL_REQUIRED;
    public static final String MSG_TOKEN_REQUIRED = ErrorMessages.Auth.TOKEN_REQUIRED;
    public static final String MSG_NEW_PASSWORD_REQUIRED = ErrorMessages.Auth.NEW_PASSWORD_REQUIRED;
    public static final String MSG_PASSWORD_RESET_SENT = SuccessMessages.Auth.PASSWORD_RESET_SENT;
    public static final String MSG_INVALID_TOKEN_OR_USER = ErrorMessages.Auth.INVALID_TOKEN_OR_USER;
    public static final String MSG_PASSWORD_UPDATED = SuccessMessages.Auth.PASSWORD_UPDATED;

    public static final int TEMP_TOKEN_MINUTES = BusinessRules.Auth.TEMP_TOKEN_MINUTES;
}