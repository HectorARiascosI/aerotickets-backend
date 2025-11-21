package com.aerotickets.constants;

public final class DtoValidationConstants {

    private DtoValidationConstants() {
    }

    public static final int LOGIN_EMAIL_MAX = 120;
    public static final int LOGIN_PASSWORD_MIN = 8;
    public static final int LOGIN_PASSWORD_MAX = 120;

    public static final int USER_FULLNAME_MIN = 3;
    public static final int USER_FULLNAME_MAX = 80;
    public static final String USER_FULLNAME_REGEX = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$";
    public static final String USER_FULLNAME_MESSAGE = "Full name can only contain letters and spaces";

    public static final int USER_EMAIL_MAX = 120;
    public static final int USER_PASSWORD_MIN = 8;
    public static final int USER_PASSWORD_MAX = 120;

    public static final int IATA_CODE_LENGTH = 3;
    public static final String IATA_CODE_REGEX = "^[A-Za-z]{3}$";
    public static final String IATA_ORIGIN_MESSAGE = "Origin must be a 3-letter IATA code";
    public static final String IATA_DESTINATION_MESSAGE = "Destination must be a 3-letter IATA code";
    public static final String FLIGHT_DATE_PAST_MESSAGE = "Flight date cannot be in the past";
}