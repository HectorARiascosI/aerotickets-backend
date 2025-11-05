package com.aerotickets.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReservationRequestDTO {
    @NotNull
    private Long flightId;

    @Positive(message = "El asiento debe ser positivo")
    private Integer seatNumber; // opcional

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }
}