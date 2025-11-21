package com.aerotickets.constants;

/**
 * @deprecated Use {@link SecurityPatterns}, {@link HttpConstants}, and {@link ConfigKeys.Cors} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String CORS_ALLOWED_ORIGINS_PROPERTY = ConfigKeys.Cors.ALLOWED_ORIGINS_PROPERTY;
    public static final String CORS_ORIGINS_SEPARATOR = ConfigKeys.Cors.ORIGINS_SEPARATOR;

    public static final String DEFAULT_ALLOWED_ORIGINS = ConfigKeys.Cors.DEFAULT_ALLOWED_ORIGINS;

    public static final String ANT_PATTERN_ALL = SecurityPatterns.ANT_PATTERN_ALL;

    public static final String HEALTH_PATH = "/health";
    public static final String HOLA_PATH = "/hola";
    public static final String ACTUATOR_HEALTH_PATH = "/actuator/health";

    public static final String AUTH_PATH = "/auth/**";
    public static final String AUTH_API_PATH = "/api/auth/**";

    public static final String LIVE_PATH = "/live/**";
    public static final String LIVE_API_PATH = "/api/live/**";

    public static final String CATALOG_PATH = "/catalog/**";
    public static final String CATALOG_API_PATH = "/api/catalog/**";

    public static final String FLIGHTS_PATH = "/flights/**";
    public static final String FLIGHTS_API_PATH = "/api/flights/**";

    public static final String[] PUBLIC_ENDPOINTS = SecurityPatterns.PUBLIC_ENDPOINTS;

    public static final String[] AUTH_ENDPOINTS = SecurityPatterns.AUTH_ENDPOINTS;

    public static final String[] LIVE_ENDPOINTS = SecurityPatterns.LIVE_ENDPOINTS;

    public static final String[] CATALOG_GET_ENDPOINTS = SecurityPatterns.CATALOG_GET_ENDPOINTS;

    public static final String[] FLIGHTS_GET_ENDPOINTS = SecurityPatterns.FLIGHTS_GET_ENDPOINTS;

    public static final String[] CORS_ALLOWED_METHODS = HttpConstants.Cors.ALLOWED_METHODS;

    public static final String[] CORS_ALLOWED_HEADERS = HttpConstants.Cors.ALLOWED_HEADERS;

    public static final String[] CORS_EXPOSED_HEADERS = HttpConstants.Cors.EXPOSED_HEADERS;

    public static final long CORS_MAX_AGE_SECONDS = HttpConstants.Cors.MAX_AGE_SECONDS;

    public static final String AUTH_PUBLIC_PREFIX = "/auth";
    public static final String AUTH_API_PUBLIC_PREFIX = "/api/auth";
    public static final String SWAGGER_PREFIX = "/swagger";
    public static final String V3_API_DOCS_PREFIX = "/v3/api-docs";

    public static final String HEADER_AUTHORIZATION = HttpConstants.Headers.AUTHORIZATION;
    public static final String BEARER_PREFIX = HttpConstants.Headers.BEARER_PREFIX;
}
