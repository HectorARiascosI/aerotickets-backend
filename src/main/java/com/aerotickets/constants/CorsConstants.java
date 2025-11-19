package com.aerotickets.constants;

public final class CorsConstants {

    private CorsConstants() {
    }

    public static final String CORS_ALLOWED_ORIGINS_ENV = "CORS_ALLOWED_ORIGINS";

    public static final String CORS_DEFAULT_ALLOWED_ORIGINS =
            "http://localhost:5173," +
            "http://127.0.0.1:5173," +
            "https://aerotickets-frontend-iaqxjc453-hector-riascos-projects.vercel.app";

    public static final String MAPPING_PATTERN = "/**";

    public static final String[] ALLOWED_METHODS = new String[] {
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS",
            "PATCH"
    };

    public static final String[] ALLOWED_HEADERS = new String[] {
            "*"
    };

    public static final String[] EXPOSED_HEADERS = new String[] {
            "Authorization",
            "Content-Type"
    };

    public static final long MAX_AGE_SECONDS = 3600L;
}