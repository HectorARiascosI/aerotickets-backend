package com.aerotickets.constants;

import java.math.BigDecimal;
import java.time.ZoneId;

public final class FlightSeedConstants {

    private FlightSeedConstants() {
    }

    public static final int SEED_DAYS_AHEAD = 210;

    public static final int FLIGHTS_PER_ROUTE_PER_DAY = 3;

    public static final ZoneId SEED_ZONE_ID = ZoneId.of(LiveFlightConstants.ZONE_ID_BOGOTA);

    public static final int[] DEFAULT_DEPARTURE_HOURS = {6, 12, 18};

    public static final BigDecimal MIN_BASE_PRICE = new BigDecimal("80000");
    public static final BigDecimal PRICE_PER_MINUTE = new BigDecimal("200");
}