package com.aerotickets.constants;

/**
 * Configuration property keys and default values.
 * Used for application.yml properties and environment variables.
 */
public final class ConfigKeys {

    private ConfigKeys() {
    }

    public static final class Environment {
        private Environment() {}
        public static final String PROFILE_KEY = "SPRING_PROFILES_ACTIVE";
        public static final String DB_URL_KEY = "DB_URL";
        public static final String PORT_KEY = "PORT";
        public static final String DEFAULT_PROFILE = "dev";
        public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/aerotickets";
        public static final String DEFAULT_PORT = "8080";
    }

    public static final class Cors {
        private Cors() {}
        public static final String ALLOWED_ORIGINS_PROPERTY = "cors.allowed-origins";
        public static final String ORIGINS_SEPARATOR = ",";
        public static final String DEFAULT_ALLOWED_ORIGINS =
                "http://localhost:5173," +
                "https://aerotickets-frontend.vercel.app," +
                "https://aerotickets-frontend-git-main-hector-riascos-projects.vercel.app," +
                "https://*.vercel.app";
        public static final String ORIGIN_LOCAL = "http://localhost:5173";
        public static final String ORIGIN_VERCEL_MAIN = "https://aerotickets-frontend.vercel.app";
        public static final String ORIGIN_VERCEL_ENV = "https://aerotickets-frontend-iaqxjc453-hector-riascos-projects.vercel.app";
    }

    public static final class Email {
        private Email() {}
        public static final String DEFAULT_FROM = "no-reply@aerotickets.com";
        public static final String SENDER_NAME = "Aerotickets";
    }

    public static final class Cache {
        private Cache() {}
        public static final long LIVE_FLIGHTS_EXPIRE_AFTER_WRITE_MINUTES = 3L;
        public static final long LIVE_FLIGHTS_MAXIMUM_SIZE = 500L;
        public static final String LIVE_FLIGHTS_CACHE_NAME = "liveFlights";
    }
}
