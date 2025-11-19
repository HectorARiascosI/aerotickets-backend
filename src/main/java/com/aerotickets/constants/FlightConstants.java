package com.aerotickets.constants;

import java.math.BigDecimal;

public final class FlightConstants {

    private FlightConstants() {
    }

    public static final String BASE_PATH = "/flights";
    public static final String SEARCH_PATH = "/search";

    public static final String MSG_MISSING_REQUIRED_FIELDS =
            "Campos obligatorios faltantes: airline, origin, destination, departureAt";

    public static final int DEFAULT_TOTAL_SEATS = 180;
    public static final int DEFAULT_DURATION_HOURS = 2;
    public static final BigDecimal DEFAULT_PRICE = BigDecimal.ZERO;

    public static final String OFFSET_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX";

    public static final String ERR_DEPARTURE_IN_PAST =
            "Departure time must be in the future";
    public static final String ERR_ARRIVAL_BEFORE_DEPARTURE =
            "Arrival time must be after departure time";
    public static final String ERR_ORIGIN_DEST_REQUIRED =
            "Origin and destination are required";
    public static final String ERR_DATE_IN_PAST =
            "Flight date cannot be in the past";

    public static final String DEFAULT_AIRLINE_NAME = "Aerolinea Demo";
}