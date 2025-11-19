package com.aerotickets.constants;

public final class LiveFlightConstants {

    private LiveFlightConstants() {
    }

    public static final String BASE_PATH = "/live";

    public static final String STREAM_PATH = "/stream";
    public static final String FLIGHTS_SEARCH_PATH = "/flights/search";
    public static final String AIRPORTS_SEARCH_PATH = "/airports/search";

    public static final String CORS_ORIGIN_LOCAL = "http://localhost:5173";
    public static final String CORS_ORIGIN_VERCEL_MAIN = "https://aerotickets-frontend.vercel.app";
    public static final String CORS_ORIGIN_VERCEL_WILDCARD = "https://*.vercel.app";

    public static final String SSE_EVENT_PING_NAME = "ping";
    public static final String SSE_EVENT_PING_DATA = "♥";

    public static final long PING_INITIAL_DELAY_SECONDS = 20L;
    public static final long PING_PERIOD_SECONDS = 20L;

    public static final String PARAM_QUERY = "query";

    public static final String ZONE_ID_BOGOTA = "America/Bogota";
    public static final String ISO_LOCAL_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String PROVIDER_DB = "db";
    public static final String FLIGHT_NUMBER_PREFIX = "FL";

    public static final int BOARDING_START_MINUTES_BEFORE = 30;
    public static final int BOARDING_END_MINUTES_BEFORE = 10;

    public static final int CARGO_BASE_KG = 500;
    public static final int CARGO_PER_PAX_KG = 15;
}