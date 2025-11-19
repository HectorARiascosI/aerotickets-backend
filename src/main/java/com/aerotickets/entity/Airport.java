package com.aerotickets.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airports_co")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airport {

    @Id
    @Column(name = "iata", length = 3)
    private String iata;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state")
    private String state;

   
}