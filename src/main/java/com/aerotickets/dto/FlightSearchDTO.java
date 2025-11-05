package com.aerotickets.dto;

import java.time.LocalDate;

public class FlightSearchDTO {
    private String origin;        // Código o nombre de aeropuerto de origen
    private String destination;   // Código o nombre de aeropuerto de destino
    private LocalDate date;       // Fecha del vuelo (si no se envía, se asume hoy)

    public FlightSearchDTO() {}

    public FlightSearchDTO(String origin, String destination, LocalDate date) {
        this.origin = origin;
        this.destination = destination;
        this.date = date;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
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