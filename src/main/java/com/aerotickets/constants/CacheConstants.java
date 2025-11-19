package com.aerotickets.constants;

public final class CacheConstants {

    private CacheConstants() {
    }

    public static final long LIVE_FLIGHTS_EXPIRE_AFTER_WRITE_MINUTES = 3L;
    public static final long LIVE_FLIGHTS_MAXIMUM_SIZE = 500L;
    public static final String LIVE_FLIGHTS_CACHE_NAME = "liveFlights";
}