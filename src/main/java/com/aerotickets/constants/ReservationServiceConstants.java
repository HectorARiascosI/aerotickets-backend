package com.aerotickets.constants;

/**
 * @deprecated Use {@link ErrorMessages.Reservation} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class ReservationServiceConstants {

    private ReservationServiceConstants() {
    }

    public static final String ERR_USER_EMAIL_REQUIRED = ErrorMessages.Reservation.USER_EMAIL_REQUIRED;
    public static final String ERR_FLIGHT_ID_REQUIRED = ErrorMessages.Reservation.FLIGHT_ID_REQUIRED;
    public static final String ERR_USER_NOT_FOUND = ErrorMessages.Reservation.USER_NOT_FOUND;
    public static final String ERR_FLIGHT_NOT_FOUND = ErrorMessages.Reservation.FLIGHT_NOT_FOUND;
    public static final String ERR_NO_SEATS_AVAILABLE = ErrorMessages.Reservation.NO_SEATS_AVAILABLE;
    public static final String ERR_SEAT_OUT_OF_RANGE = ErrorMessages.Reservation.SEAT_OUT_OF_RANGE;
    public static final String ERR_SEAT_TAKEN = ErrorMessages.Reservation.SEAT_TAKEN;
    public static final String ERR_ACTIVE_RESERVATION_OR_SEAT_TAKEN = ErrorMessages.Reservation.ACTIVE_RESERVATION_OR_SEAT_TAKEN;
    public static final String ERR_USER_ALREADY_HAS_ACTIVE_RESERVATION = ErrorMessages.Reservation.USER_ALREADY_HAS_ACTIVE_RESERVATION;
    public static final String ERR_RESERVATION_ID_REQUIRED = ErrorMessages.Reservation.RESERVATION_ID_REQUIRED;
    public static final String ERR_RESERVATION_NOT_FOUND = ErrorMessages.Reservation.RESERVATION_NOT_FOUND;
    public static final String ERR_USER_EMAIL_AND_FLIGHT_ID_REQUIRED = ErrorMessages.Reservation.USER_EMAIL_AND_FLIGHT_ID_REQUIRED;
    public static final String ERR_USER_EMAIL_FLIGHT_ID_SEAT_REQUIRED = ErrorMessages.Reservation.USER_EMAIL_FLIGHT_ID_SEAT_REQUIRED;
    public static final String ERR_ACTIVE_RESERVATION_FOR_SEAT_NOT_FOUND = ErrorMessages.Reservation.ACTIVE_RESERVATION_FOR_SEAT_NOT_FOUND;
}