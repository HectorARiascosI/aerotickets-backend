package com.aerotickets.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airlines_co")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airline {

    @Id
    @Column(name = "code", length = 3)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;
}