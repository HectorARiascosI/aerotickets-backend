package com.aerotickets.constants;

/**
 * @deprecated Use {@link ApiPaths.Reservations} and {@link ErrorMessages.Reservation} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class ReservationConstants {

    private ReservationConstants() {
    }

    public static final String BASE_PATH = ApiPaths.Reservations.BASE;
    public static final String MY_PATH = ApiPaths.Reservations.MY;
    public static final String ME_PATH = ApiPaths.Reservations.ME;

    public static final String MSG_SEAT_MUST_BE_POSITIVE = ErrorMessages.Reservation.SEAT_MUST_BE_POSITIVE;
}