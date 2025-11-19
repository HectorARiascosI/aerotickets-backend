package com.aerotickets.dto;

import com.aerotickets.constants.ReservationConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReservationRequestDTO {

    @NotNull
    private Long flightId;

    @Positive(message = ReservationConstants.MSG_SEAT_MUST_BE_POSITIVE)
    private Integer seatNumber;

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }
}