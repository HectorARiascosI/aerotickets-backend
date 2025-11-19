package com.aerotickets.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airline_fleet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirlineFleet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "airline_name", nullable = false, length = 100)
    private String airlineName;

    @Column(name = "airline_code", nullable = false, length = 5)
    private String airlineCode;

    @Column(name = "aircraft_type", nullable = false, length = 50)
    private String aircraftType;

    @Column(name = "typical_seats")
    private Integer typicalSeats;
}