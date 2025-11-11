package com.aerotickets.controller;

import com.aerotickets.dto.FlightDTO;
import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.entity.Flight;
import com.aerotickets.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
        if (dto.getAirline() == null ||
            dto.getOrigin() == null ||
            dto.getDestination() == null ||
            dto.getDepartureAt() == null) {
            throw new IllegalArgumentException(
                "Campos obligatorios faltantes: airline, origin, destination, departureAt"
            );
        }

        // Convertimos OffsetDateTime -> LocalDateTime en UTC
        LocalDateTime dep = dto.getDepartureAt()
                .atZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();

        LocalDateTime arr = (dto.getArriveAt() != null)
                ? dto.getArriveAt().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
                : dep.plusHours(2);

        Integer seats = (dto.getTotalSeats() != null) ? dto.getTotalSeats() : 180;
        BigDecimal price = (dto.getPrice() != null) ? dto.getPrice() : BigDecimal.ZERO;

        Flight f = Flight.builder()
                .airline(dto.getAirline())
                .origin(dto.getOrigin())
                .destination(dto.getDestination())
                .departureAt(dep)
                .arriveAt(arr)
                .totalSeats(seats)
                .price(price)
                .build();

        return ResponseEntity.ok(flightService.create(f));
    }

    @PostMapping("/search")
    public ResponseEntity<List<Flight>> search(@RequestBody FlightSearchDTO dto) {
        return ResponseEntity.ok(flightService.searchOrSimulate(dto));
    }
}
