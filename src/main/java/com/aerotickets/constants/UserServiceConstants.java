package com.aerotickets.constants;

/**
 * @deprecated Use {@link ErrorMessages.Auth} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class UserServiceConstants {

    private UserServiceConstants() {
    }

    public static final String ERR_EMAIL_ALREADY_REGISTERED = ErrorMessages.Auth.EMAIL_ALREADY_REGISTERED;
}