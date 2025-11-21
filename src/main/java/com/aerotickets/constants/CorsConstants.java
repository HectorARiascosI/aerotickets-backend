package com.aerotickets.constants;

/**
 * @deprecated Use {@link ConfigKeys.Cors} and {@link HttpConstants.Cors} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class CorsConstants {

    private CorsConstants() {
    }

    public static final String CORS_ALLOWED_ORIGINS_ENV = "CORS_ALLOWED_ORIGINS";

    public static final String CORS_DEFAULT_ALLOWED_ORIGINS = ConfigKeys.Cors.DEFAULT_ALLOWED_ORIGINS;

    public static final String MAPPING_PATTERN = SecurityPatterns.ANT_PATTERN_ALL;

    public static final String[] ALLOWED_METHODS = HttpConstants.Cors.ALLOWED_METHODS;

    public static final String[] ALLOWED_HEADERS = new String[] { "*" };

    public static final String[] EXPOSED_HEADERS = HttpConstants.Cors.EXPOSED_HEADERS;

    public static final long MAX_AGE_SECONDS = HttpConstants.Cors.MAX_AGE_SECONDS;
}
