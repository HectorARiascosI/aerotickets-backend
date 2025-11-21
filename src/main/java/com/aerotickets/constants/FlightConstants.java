package com.aerotickets.constants;

import java.math.BigDecimal;

/**
 * @deprecated Use {@link ApiPaths.Flights}, {@link ErrorMessages.Flight}, and {@link BusinessRules.Flight} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class FlightConstants {

    private FlightConstants() {
    }

    public static final String BASE_PATH = ApiPaths.Flights.BASE;
    public static final String SEARCH_PATH = ApiPaths.Flights.SEARCH;

    public static final String MSG_MISSING_REQUIRED_FIELDS = ErrorMessages.Flight.MISSING_REQUIRED_FIELDS;

    public static final int DEFAULT_TOTAL_SEATS = BusinessRules.Flight.DEFAULT_TOTAL_SEATS;
    public static final int DEFAULT_DURATION_HOURS = BusinessRules.Flight.DEFAULT_DURATION_HOURS;
    public static final BigDecimal DEFAULT_PRICE = BusinessRules.Flight.DEFAULT_PRICE;

    public static final String OFFSET_DATETIME_PATTERN = BusinessRules.Flight.OFFSET_DATETIME_PATTERN;

    public static final String ERR_DEPARTURE_IN_PAST = ErrorMessages.Flight.DEPARTURE_IN_PAST;
    public static final String ERR_ARRIVAL_BEFORE_DEPARTURE = ErrorMessages.Flight.ARRIVAL_BEFORE_DEPARTURE;
    public static final String ERR_ORIGIN_DEST_REQUIRED = ErrorMessages.Flight.ORIGIN_DEST_REQUIRED;
    public static final String ERR_DATE_IN_PAST = ErrorMessages.Flight.DATE_IN_PAST;

    public static final String DEFAULT_AIRLINE_NAME = BusinessRules.Flight.DEFAULT_AIRLINE_NAME;
}