package com.aerotickets.constants;

/**
 * API endpoint paths for all controllers.
 * Centralized location for all route definitions.
 */
public final class ApiPaths {

    private ApiPaths() {
    }

    public static final class Auth {
        private Auth() {}
        public static final String BASE = "/auth";
        public static final String REGISTER = "/register";
        public static final String LOGIN = "/login";
        public static final String FORGOT_PASSWORD = "/forgot-password";
        public static final String RESET_PASSWORD = "/reset-password";
    }

    public static final class Users {
        private Users() {}
        public static final String BASE = "/users";
        public static final String REGISTER = "/register";
    }

    public static final class Flights {
        private Flights() {}
        public static final String BASE = "/flights";
        public static final String SEARCH = "/search";
    }

    public static final class Reservations {
        private Reservations() {}
        public static final String BASE = "/reservations";
        public static final String MY = "/my";
        public static final String ME = "/me";
    }

    public static final class Payments {
        private Payments() {}
        public static final String BASE = "/payments";
        public static final String CHECKOUT_SESSION = "/checkout-session";
    }

    public static final class Catalog {
        private Catalog() {}
        public static final String BASE = "/catalog";
        public static final String AIRLINES_CO = "/airlines/co";
        public static final String AIRPORTS_CO = "/airports/co";
    }

    public static final class Live {
        private Live() {}
        public static final String BASE = "/live";
        public static final String AIRPORTS_SEARCH = "/airports/search";
        public static final String FLIGHTS_SEARCH = "/flights/search";
        public static final String STREAM = "/stream";
    }

    public static final class Health {
        private Health() {}
        public static final String BASE = "/health";
        public static final String HOLA = "/hola";
        public static final String ACTUATOR = "/actuator/health";
    }
}
