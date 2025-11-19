package com.aerotickets.constants;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String CORS_ALLOWED_ORIGINS_PROPERTY = "cors.allowed-origins";
    public static final String CORS_ORIGINS_SEPARATOR = ",";

    public static final String DEFAULT_ALLOWED_ORIGINS =
            "http://localhost:5173," +
            "https://aerotickets-frontend.vercel.app," +
            "https://aerotickets-frontend-git-main-hector-riascos-projects.vercel.app," +
            "https://*.vercel.app";

    public static final String ANT_PATTERN_ALL = "/**";

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

    public static final String[] PUBLIC_ENDPOINTS = {
            HEALTH_PATH,
            HOLA_PATH,
            ACTUATOR_HEALTH_PATH
    };

    public static final String[] AUTH_ENDPOINTS = {
            AUTH_PATH,
            AUTH_API_PATH
    };

    public static final String[] LIVE_ENDPOINTS = {
            LIVE_PATH,
            LIVE_API_PATH
    };

    public static final String[] CATALOG_GET_ENDPOINTS = {
            CATALOG_PATH,
            CATALOG_API_PATH
    };

    public static final String[] FLIGHTS_GET_ENDPOINTS = {
            FLIGHTS_PATH,
            FLIGHTS_API_PATH
    };

    public static final String[] CORS_ALLOWED_METHODS = {
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH",
            "OPTIONS"
    };

    public static final String[] CORS_ALLOWED_HEADERS = {
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With"
    };

    public static final String[] CORS_EXPOSED_HEADERS = {
            "Authorization",
            "Content-Type"
    };

    public static final long CORS_MAX_AGE_SECONDS = 3600L;

    public static final String AUTH_PUBLIC_PREFIX = "/auth";
    public static final String AUTH_API_PUBLIC_PREFIX = "/api/auth";
    public static final String SWAGGER_PREFIX = "/swagger";
    public static final String V3_API_DOCS_PREFIX = "/v3/api-docs";

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
}