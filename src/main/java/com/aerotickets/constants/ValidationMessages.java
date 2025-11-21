package com.aerotickets.constants;

/**
 * Validation messages for DTO and entity validation.
 * Used with Jakarta Bean Validation annotations.
 */
public final class ValidationMessages {

    private ValidationMessages() {
    }

    public static final class Common {
        private Common() {}
        public static final String NOT_NULL = "Field cannot be null";
        public static final String NOT_BLANK = "Field cannot be blank";
        public static final String NOT_EMPTY = "Field cannot be empty";
    }

    public static final class Email {
        private Email() {}
        public static final String INVALID_FORMAT = "Invalid email format";
        public static final String REQUIRED = "Email is required";
    }

    public static final class Password {
        private Password() {}
        public static final String MIN_LENGTH = "Password must be at least {min} characters";
        public static final String REQUIRED = "Password is required";
    }

    public static final class Flight {
        private Flight() {}
        public static final String AIRLINE_REQUIRED = "Airline is required";
        public static final String ORIGIN_REQUIRED = "Origin is required";
        public static final String DESTINATION_REQUIRED = "Destination is required";
        public static final String DEPARTURE_REQUIRED = "Departure time is required";
        public static final String SEATS_POSITIVE = "Total seats must be positive";
        public static final String PRICE_POSITIVE = "Price must be positive or zero";
    }

    public static final class Reservation {
        private Reservation() {}
        public static final String FLIGHT_ID_REQUIRED = "Flight ID is required";
        public static final String SEAT_POSITIVE = "Seat number must be positive";
    }
}
