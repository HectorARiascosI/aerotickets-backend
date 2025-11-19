package com.aerotickets.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "route_profiles_co")
public class RouteProfileCo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origin_iata", length = 3, nullable = false)
    private String originIata;

    @Column(name = "destination_iata", length = 3, nullable = false)
    private String destinationIata;

    @Column(name = "typical_minutes", nullable = false)
    private Integer typicalMinutes;

    public Long getId() {
        return id;
    }

    public String getOriginIata() {
        return originIata;
    }

    public void setOriginIata(String originIata) {
        this.originIata = originIata;
    }

    public String getDestinationIata() {
        return destinationIata;
    }

    public void setDestinationIata(String destinationIata) {
        this.destinationIata = destinationIata;
    }

    public Integer getTypicalMinutes() {
        return typicalMinutes;
    }

    public void setTypicalMinutes(Integer typicalMinutes) {
        this.typicalMinutes = typicalMinutes;
    }
}