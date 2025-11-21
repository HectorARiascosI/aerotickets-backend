package com.aerotickets.constants;

/**
 * @deprecated Use {@link ErrorMessages.Jwt} and {@link BusinessRules.Auth} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class JwtConstants {

    private JwtConstants() {
    }

    public static final String SECRET_TOO_SHORT_PREFIX = ErrorMessages.Jwt.SECRET_TOO_SHORT_PREFIX;

    public static final String CLAIM_NAME = "name";

    public static final String TEMP_TOKEN_INVALID_OR_EXPIRED = ErrorMessages.Jwt.TEMP_TOKEN_INVALID_OR_EXPIRED;
}