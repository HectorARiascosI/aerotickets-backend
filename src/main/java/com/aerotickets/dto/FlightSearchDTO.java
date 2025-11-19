package com.aerotickets.dto;

import com.aerotickets.constants.DtoValidationConstants;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class FlightSearchDTO {

    @NotBlank
    @Size(min = DtoValidationConstants.IATA_CODE_LENGTH, max = DtoValidationConstants.IATA_CODE_LENGTH)
    @Pattern(
            regexp = DtoValidationConstants.IATA_CODE_REGEX,
            message = DtoValidationConstants.IATA_ORIGIN_MESSAGE
    )
    private String origin;

    @NotBlank
    @Size(min = DtoValidationConstants.IATA_CODE_LENGTH, max = DtoValidationConstants.IATA_CODE_LENGTH)
    @Pattern(
            regexp = DtoValidationConstants.IATA_CODE_REGEX,
            message = DtoValidationConstants.IATA_DESTINATION_MESSAGE
    )
    private String destination;

    @FutureOrPresent(message = DtoValidationConstants.FLIGHT_DATE_PAST_MESSAGE)
    private LocalDate date;

    public FlightSearchDTO() {
    }

    public FlightSearchDTO(String origin, String destination, LocalDate date) {
        this.origin = origin;
        this.destination = destination;
        this.date = date;
    }

    public String getOrigin() {
        return origin != null ? origin.trim().toUpperCase() : null;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination != null ? destination.trim().toUpperCase() : null;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "FlightSearchDTO{" +
                "origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", date=" + date +
                '}';
    }
}