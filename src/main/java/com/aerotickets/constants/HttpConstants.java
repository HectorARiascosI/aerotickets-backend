package com.aerotickets.constants;

/**
 * HTTP-related constants including headers, methods, and status codes.
 */
public final class HttpConstants {

    private HttpConstants() {
    }

    public static final class Headers {
        private Headers() {}
        public static final String AUTHORIZATION = "Authorization";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCEPT = "Accept";
        public static final String ORIGIN = "Origin";
        public static final String X_REQUESTED_WITH = "X-Requested-With";
        public static final String BEARER_PREFIX = "Bearer ";
    }

    public static final class Methods {
        private Methods() {}
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
        public static final String PATCH = "PATCH";
        public static final String OPTIONS = "OPTIONS";
    }

    public static final class ContentTypes {
        private ContentTypes() {}
        public static final String APPLICATION_JSON = "application/json";
        public static final String TEXT_PLAIN = "text/plain";
        public static final String TEXT_HTML = "text/html";
    }

    public static final class Cors {
        private Cors() {}
        public static final String[] ALLOWED_METHODS = {
                Methods.GET,
                Methods.POST,
                Methods.PUT,
                Methods.DELETE,
                Methods.PATCH,
                Methods.OPTIONS
        };
        public static final String[] ALLOWED_HEADERS = {
                Headers.AUTHORIZATION,
                Headers.CONTENT_TYPE,
                Headers.ACCEPT,
                Headers.ORIGIN,
                Headers.X_REQUESTED_WITH
        };
        public static final String[] EXPOSED_HEADERS = {
                Headers.AUTHORIZATION,
                Headers.CONTENT_TYPE
        };
        public static final long MAX_AGE_SECONDS = 3600L;
    }
}
