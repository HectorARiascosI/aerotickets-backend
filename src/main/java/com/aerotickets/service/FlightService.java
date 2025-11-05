package com.aerotickets.service;

import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.entity.Flight;
import com.aerotickets.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
public class FlightService {
    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository){ this.flightRepository = flightRepository; }

    @Transactional
    public Flight create(Flight f) { return flightRepository.save(f); }

    public List<Flight> listAll() { return flightRepository.findAll(); }

    public List<Flight> searchOrSimulate(FlightSearchDTO dto) {
        if (dto.getOrigin()==null || dto.getDestination()==null) return List.of();
        String dep = dto.getOrigin();
        String arr = dto.getDestination();
        LocalDate date = dto.getDate() != null ? dto.getDate() : LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return flightRepository.findByOriginAndDestinationAndDepartureAtBetween(dep, arr, start, end);
    }
}