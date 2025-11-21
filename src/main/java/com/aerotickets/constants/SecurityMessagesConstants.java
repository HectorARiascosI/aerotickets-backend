package com.aerotickets.constants;

/**
 * @deprecated Use {@link ErrorMessages.Auth} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class SecurityMessagesConstants {

    private SecurityMessagesConstants() {
    }

    public static final String ERR_USER_NOT_FOUND_PREFIX = ErrorMessages.Auth.USER_NOT_FOUND_PREFIX;
}