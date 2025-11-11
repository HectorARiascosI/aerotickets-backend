package com.aerotickets.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDTO {
    private Long id;

    private String airline;
    private String origin;
    private String destination;

    // Acepta y emite "yyyy-MM-dd'T'HH:mm:ss" (sin Z)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arriveAt;

    // Pueden venir null; el controller pone default seguros
    private Integer totalSeats;
    private BigDecimal price;
}