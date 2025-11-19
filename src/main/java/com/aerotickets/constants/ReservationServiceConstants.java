package com.aerotickets.constants;

public final class ReservationServiceConstants {

    private ReservationServiceConstants() {
    }

    public static final String ERR_USER_EMAIL_REQUIRED = "User email is required";
    public static final String ERR_FLIGHT_ID_REQUIRED = "Flight id is required";
    public static final String ERR_USER_NOT_FOUND = "User not found";
    public static final String ERR_FLIGHT_NOT_FOUND = "Flight not found";
    public static final String ERR_NO_SEATS_AVAILABLE = "No seats available for this flight";
    public static final String ERR_SEAT_OUT_OF_RANGE = "Seat number out of range";
    public static final String ERR_SEAT_TAKEN = "Selected seat is already reserved";
    public static final String ERR_ACTIVE_RESERVATION_OR_SEAT_TAKEN =
            "You already have an ACTIVE reservation for this flight or the seat is taken";
    public static final String ERR_RESERVATION_ID_REQUIRED = "Reservation id is required";
    public static final String ERR_RESERVATION_NOT_FOUND = "Reservation not found";
    public static final String ERR_USER_EMAIL_AND_FLIGHT_ID_REQUIRED =
            "User email and flight id are required";
    public static final String ERR_USER_EMAIL_FLIGHT_ID_SEAT_REQUIRED =
            "User email, flight id and seat number are required";
    public static final String ERR_ACTIVE_RESERVATION_FOR_SEAT_NOT_FOUND =
            "Active reservation for that seat not found";
}