																																		package com.aerotickets.constants;

import java.math.BigDecimal;

public final class DataLoaderConstants {

    private DataLoaderConstants() {
    }

    public static final long FLIGHT_1_DAYS_OFFSET = 3L;
    public static final int FLIGHT_1_DEPARTURE_HOUR = 8;
    public static final int FLIGHT_1_DEPARTURE_MINUTE = 0;
    public static final int FLIGHT_1_ARRIVAL_HOUR = 9;
    public static final int FLIGHT_1_ARRIVAL_MINUTE = 15;
    public static final int FLIGHT_1_TOTAL_SEATS = 150;
    public static final BigDecimal FLIGHT_1_PRICE = new BigDecimal("220000");
    public static final String FLIGHT_1_AIRLINE = "Aerolíneas Demo";
    public static final String FLIGHT_1_ORIGIN = "BOG";
    public static final String FLIGHT_1_DESTINATION = "MDE";

    public static final long FLIGHT_2_DAYS_OFFSET = 7L;
    public static final int FLIGHT_2_DEPARTURE_HOUR = 10;
    public static final int FLIGHT_2_DEPARTURE_MINUTE = 30;
    public static final int FLIGHT_2_ARRIVAL_HOUR = 12;
    public static final int FLIGHT_2_ARRIVAL_MINUTE = 0;
    public static final int FLIGHT_2_TOTAL_SEATS = 120;
    public static final BigDecimal FLIGHT_2_PRICE = new BigDecimal("380000");
    public static final String FLIGHT_2_AIRLINE = "Demo Air";
    public static final String FLIGHT_2_ORIGIN = "CLO";
    public static final String FLIGHT_2_DESTINATION = "CTG";
}