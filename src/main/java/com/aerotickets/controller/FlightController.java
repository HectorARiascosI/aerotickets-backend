package com.aerotickets.controller;

import com.aerotickets.dto.FlightDTO;
import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.entity.Flight;
import com.aerotickets.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService){
        this.flightService = flightService;
    }

    @GetMapping
    public List<Flight> listAll() {
        return flightService.listAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody FlightDTO dto) {
        Flight f = Flight.builder()
                .airline(dto.getAirline())
                .origin(dto.getOrigin())
                .destination(dto.getDestination())
                .departureAt(dto.getDepartureAt())
                .arriveAt(dto.getArriveAt())
                // Defaults seguros para evitar NPE/violaciones de BD
                .totalSeats(dto.getTotalSeats() != null ? dto.getTotalSeats() : 0)
                .price(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO)
                .build();
        return ResponseEntity.ok(flightService.create(f));
    }

    @PostMapping("/search")
    public ResponseEntity<List<Flight>> search(@RequestBody FlightSearchDTO dto) {
        return ResponseEntity.ok(flightService.searchOrSimulate(dto));
    }
}