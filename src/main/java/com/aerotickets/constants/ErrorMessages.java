package com.aerotickets.constants;

/**
 * Error messages for exceptions and validation.
 * All user-facing error messages should be defined here.
 */
public final class ErrorMessages {

    private ErrorMessages() {
    }

    public static final class Auth {
        private Auth() {}
        public static final String EMAIL_ALREADY_REGISTERED = "Email is already registered";
        public static final String USER_NOT_FOUND = "User not found";
        public static final String EMAIL_REQUIRED = "Email is required";
        public static final String TOKEN_REQUIRED = "Recovery token is required";
        public static final String NEW_PASSWORD_REQUIRED = "New password is required";
        public static final String INVALID_TOKEN_OR_USER = "Invalid token or user not found";
        public static final String USER_NOT_FOUND_PREFIX = "User not found: ";
    }

    public static final class Flight {
        private Flight() {}
        public static final String MISSING_REQUIRED_FIELDS = "Missing required fields: airline, origin, destination, departureAt";
        public static final String DEPARTURE_IN_PAST = "Departure time must be in the future";
        public static final String ARRIVAL_BEFORE_DEPARTURE = "Arrival time must be after departure time";
        public static final String ORIGIN_DEST_REQUIRED = "Origin and destination are required";
        public static final String DATE_IN_PAST = "Flight date cannot be in the past";
    }

    public static final class Reservation {
        private Reservation() {}
        public static final String USER_EMAIL_REQUIRED = "User email is required";
        public static final String FLIGHT_ID_REQUIRED = "Flight id is required";
        public static final String USER_NOT_FOUND = "User not found";
        public static final String FLIGHT_NOT_FOUND = "Flight not found";
        public static final String NO_SEATS_AVAILABLE = "No seats available for this flight";
        public static final String SEAT_OUT_OF_RANGE = "Seat number out of range";
        public static final String SEAT_TAKEN = "Selected seat is already reserved";
        public static final String ACTIVE_RESERVATION_OR_SEAT_TAKEN = "You already have an active reservation for this flight or the seat is taken";
        public static final String RESERVATION_ID_REQUIRED = "Reservation id is required";
        public static final String RESERVATION_NOT_FOUND = "Reservation not found";
        public static final String USER_EMAIL_AND_FLIGHT_ID_REQUIRED = "User email and flight id are required";
        public static final String USER_EMAIL_FLIGHT_ID_SEAT_REQUIRED = "User email, flight id and seat number are required";
        public static final String ACTIVE_RESERVATION_FOR_SEAT_NOT_FOUND = "Active reservation for that seat not found";
        public static final String SEAT_MUST_BE_POSITIVE = "Seat number must be positive";
    }

    public static final class Jwt {
        private Jwt() {}
        public static final String SECRET_TOO_SHORT_PREFIX = "JWT secret must be at least 32 characters (256 bits). Current length: ";
        public static final String TEMP_TOKEN_INVALID_OR_EXPIRED = "Invalid or expired token";
    }
}
