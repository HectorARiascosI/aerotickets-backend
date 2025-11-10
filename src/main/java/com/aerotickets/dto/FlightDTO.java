package com.aerotickets.dto;

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
    private LocalDateTime departureAt;
    private LocalDateTime arriveAt;   // Backend usa arriveAt
    private Integer totalSeats;       // Puede venir null: el controller fija default
    private BigDecimal price;         // Puede venir null: el controller fija default
}