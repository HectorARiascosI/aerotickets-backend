package com.aerotickets.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "route_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_route_origin_dest",
                        columnNames = {"origin_iata", "destination_iata"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origin_iata", nullable = false, length = 3)
    private String originIata;

    @Column(name = "destination_iata", nullable = false, length = 3)
    private String destinationIata;

    @Column(name = "typical_minutes", nullable = false)
    private Integer typicalMinutes;
}