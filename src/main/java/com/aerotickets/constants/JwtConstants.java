package com.aerotickets.constants;

public final class JwtConstants {

    private JwtConstants() {
    }

    public static final String SECRET_TOO_SHORT_PREFIX =
            "JWT secret must be at least 32 characters (256 bits). Current length: ";

    public static final String CLAIM_NAME = "name";

    public static final String TEMP_TOKEN_INVALID_OR_EXPIRED = "Invalid or expired token";
}