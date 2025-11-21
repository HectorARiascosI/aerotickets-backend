package com.aerotickets.constants;

/**
 * @deprecated Use {@link ConfigKeys.Cache} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class CacheConstants {

    private CacheConstants() {
    }

    public static final long LIVE_FLIGHTS_EXPIRE_AFTER_WRITE_MINUTES = ConfigKeys.Cache.LIVE_FLIGHTS_EXPIRE_AFTER_WRITE_MINUTES;
    public static final long LIVE_FLIGHTS_MAXIMUM_SIZE = ConfigKeys.Cache.LIVE_FLIGHTS_MAXIMUM_SIZE;
    public static final String LIVE_FLIGHTS_CACHE_NAME = ConfigKeys.Cache.LIVE_FLIGHTS_CACHE_NAME;
}