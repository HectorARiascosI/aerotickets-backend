package com.aerotickets.constants;

/**
 * Security-related patterns and configurations.
 * Defines public endpoints and security rules.
 */
public final class SecurityPatterns {

    private SecurityPatterns() {
    }

    public static final String ANT_PATTERN_ALL = "/**";

    public static final String[] PUBLIC_ENDPOINTS = {
            "/health",
            "/hola",
            "/actuator/health"
    };

    public static final String[] AUTH_ENDPOINTS = {
            "/auth/**",
            "/api/auth/**"
    };

    public static final String[] LIVE_ENDPOINTS = {
            "/live/**",
            "/api/live/**"
    };

    public static final String[] CATALOG_GET_ENDPOINTS = {
            "/catalog/**",
            "/api/catalog/**"
    };

    public static final String[] FLIGHTS_GET_ENDPOINTS = {
            "/flights/**",
            "/api/flights/**"
    };

    public static final String[] PUBLIC_PREFIXES = {
            "/auth",
            "/api/auth",
            "/swagger",
            "/v3/api-docs"
    };
}
