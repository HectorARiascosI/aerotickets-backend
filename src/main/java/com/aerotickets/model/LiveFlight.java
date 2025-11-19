package com.aerotickets.model;

import lombok.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveFlight {

    private String provider;
    private String airline;
    private String airlineCode;

    private String flightNumber;
    private String originIata;
    private String destinationIata;

    private String departureAt;
    private String arrivalAt;

    private String status;

    private String aircraftType;
    private String terminal;
    private String gate;
    private String baggageBelt;

    private Integer delayMinutes;
    private boolean diverted;
    private boolean emergency;

    private int totalSeats;
    private int occupiedSeats;
    private int cargoKg;

    private String boardingStartAt;
    private String boardingEndAt;

    public double getOccupancyRate() {
        return totalSeats == 0 ? 0 : (double) occupiedSeats / totalSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LiveFlight lf)) return false;
        return Objects.equals(flightNumber, lf.flightNumber)
                && Objects.equals(originIata, lf.originIata)
                && Objects.equals(destinationIata, lf.destinationIata)
                && Objects.equals(departureAt, lf.departureAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightNumber, originIata, destinationIata, departureAt);
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s → %s) %s [%s]",
                airline, flightNumber, originIata, destinationIata, status, aircraftType);
    }
}