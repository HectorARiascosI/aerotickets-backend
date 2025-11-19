package com.aerotickets.controller;

import com.aerotickets.constants.CatalogConstants;
import com.aerotickets.entity.Airport;
import com.aerotickets.entity.Airline;
import com.aerotickets.repository.AirportRepository;
import com.aerotickets.repository.AirlineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(CatalogConstants.BASE_PATH)
public class AirCatalogController {

    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;

    public AirCatalogController(AirportRepository airportRepository,
                                AirlineRepository airlineRepository) {
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
    }

    @GetMapping(CatalogConstants.AIRPORTS_CO_PATH)
    public ResponseEntity<List<Map<String, String>>> airportsColombia() {

        List<Airport> airports = airportRepository.findAll();

        List<Map<String, String>> list = airports.stream()
                .map(a -> Map.of(
                        CatalogConstants.FIELD_IATA, a.getIata(),
                        CatalogConstants.FIELD_NAME, a.getName(),
                        CatalogConstants.FIELD_CITY, a.getCity(),
                        CatalogConstants.FIELD_COUNTRY, CatalogConstants.COUNTRY_COLOMBIA
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @GetMapping(CatalogConstants.AIRLINES_CO_PATH)
    public ResponseEntity<List<Map<String, String>>> airlinesColombia() {

        List<Airline> airlines = airlineRepository.findAll();

        List<Map<String, String>> list = airlines.stream()
                .map(a -> Map.of(
                        CatalogConstants.FIELD_CODE, a.getCode(),
                        CatalogConstants.FIELD_NAME, a.getName()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }
}