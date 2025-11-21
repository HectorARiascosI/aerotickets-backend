package com.aerotickets.dto;

import com.aerotickets.entity.ReservationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public class ReservationResponseDTO {

    private Long id;
    private String seatNumber;
    private ReservationStatus status;
    private Boolean paid;
    private Instant paidAt;
    private Instant createdAt;

    private Long flightId;
    private String airline;
    private String origin;
    private String destination;
    private LocalDateTime departureAt;
    private LocalDateTime arriveAt;
    private BigDecimal price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }
    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDateTime getDepartureAt() { return departureAt; }
    public void setDepartureAt(LocalDateTime departureAt) { this.departureAt = departureAt; }
    public LocalDateTime getArriveAt() { return arriveAt; }
    public void setArriveAt(LocalDateTime arriveAt) { this.arriveAt = arriveAt; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}