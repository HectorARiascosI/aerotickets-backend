package com.aerotickets.model;

/**
 * Representa un vuelo simulado o en vivo en el sistema Aerotickets.
 * Contiene información de la aerolínea, horarios, estado, aeronave y carga.
 * Preparado para integración con el simulador de vuelos realista.
 */
public class LiveFlight {

    private String provider;
    private String airline;
    private String airlineCode;   // ✅ nuevo campo (AV, LA, VE, etc.)
    private String flightNumber;
    private String originIata;
    private String destinationIata;
    private String departureAt;   // ISO local datetime
    private String arrivalAt;     // ISO local datetime
    private String status;        // SCHEDULED, BOARDING, EN-ROUTE, DELAYED, LANDED, DIVERTED, CANCELLED

    private String aircraftType;  // A320, B737-800, ATR 72-600...
    private String terminal;      // T1, T2
    private String gate;          // A12
    private String baggageBelt;   // 5
    private Integer delayMinutes; // null si no hay
    private boolean diverted;
    private boolean emergency;
    private int totalSeats;
    private int occupiedSeats;
    private int cargoKg;
    private String boardingStartAt;
    private String boardingEndAt;

    public LiveFlight() {}

    public LiveFlight(String provider, String airline, String flightNumber,
                      String originIata, String destinationIata,
                      String departureAt, String arrivalAt, String status) {
        this.provider = provider;
        this.airline = airline;
        this.flightNumber = flightNumber;
        this.originIata = originIata;
        this.destinationIata = destinationIata;
        this.departureAt = departureAt;
        this.arrivalAt = arrivalAt;
        this.status = status;
    }

    // ==== Getters y Setters ====

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }

    public String getAirlineCode() { return airlineCode; }
    public void setAirlineCode(String airlineCode) { this.airlineCode = airlineCode; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getOriginIata() { return originIata; }
    public void setOriginIata(String originIata) { this.originIata = originIata; }

    public String getDestinationIata() { return destinationIata; }
    public void setDestinationIata(String destinationIata) { this.destinationIata = destinationIata; }

    public String getDepartureAt() { return departureAt; }
    public void setDepartureAt(String departureAt) { this.departureAt = departureAt; }

    public String getArrivalAt() { return arrivalAt; }
    public void setArrivalAt(String arrivalAt) { this.arrivalAt = arrivalAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAircraftType() { return aircraftType; }
    public void setAircraftType(String aircraftType) { this.aircraftType = aircraftType; }

    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }

    public String getGate() { return gate; }
    public void setGate(String gate) { this.gate = gate; }

    public String getBaggageBelt() { return baggageBelt; }
    public void setBaggageBelt(String baggageBelt) { this.baggageBelt = baggageBelt; }

    public Integer getDelayMinutes() { return delayMinutes; }
    public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }

    public boolean isDiverted() { return diverted; }
    public void setDiverted(boolean diverted) { this.diverted = diverted; }

    public boolean isEmergency() { return emergency; }
    public void setEmergency(boolean emergency) { this.emergency = emergency; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getOccupiedSeats() { return occupiedSeats; }
    public void setOccupiedSeats(int occupiedSeats) { this.occupiedSeats = occupiedSeats; }

    public int getCargoKg() { return cargoKg; }
    public void setCargoKg(int cargoKg) { this.cargoKg = cargoKg; }

    public String getBoardingStartAt() { return boardingStartAt; }
    public void setBoardingStartAt(String boardingStartAt) { this.boardingStartAt = boardingStartAt; }

    public String getBoardingEndAt() { return boardingEndAt; }
    public void setBoardingEndAt(String boardingEndAt) { this.boardingEndAt = boardingEndAt; }

    // ==== Métodos auxiliares ====

    /** Devuelve el porcentaje de ocupación de asientos (0.0 - 1.0) */
    public double getOccupancyRate() {
        if (totalSeats == 0) return 0;
        return (double) occupiedSeats / totalSeats;
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s (%s → %s) %s [%s]",
                airline, flightNumber, originIata, destinationIata, status, aircraftType
        );
    }
}