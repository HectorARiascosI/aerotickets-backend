package com.aerotickets.service;

import com.aerotickets.constants.FlightConstants;
import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.entity.Flight;
import com.aerotickets.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository){
        this.flightRepository = flightRepository;
    }

    @Transactional
    public Flight create(Flight f) {
        LocalDateTime now = LocalDateTime.now();
        if (f.getDepartureAt() == null || f.getDepartureAt().isBefore(now)) {
            throw new IllegalArgumentException(FlightConstants.ERR_DEPARTURE_IN_PAST);
        }
        if (f.getArriveAt() != null && f.getArriveAt().isBefore(f.getDepartureAt())) {
            throw new IllegalArgumentException(FlightConstants.ERR_ARRIVAL_BEFORE_DEPARTURE);
        }
        return flightRepository.save(f);
    }

    @Transactional(readOnly = true)
    public List<Flight> listAll() {
        return flightRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Flight> searchOrSimulate(FlightSearchDTO dto) {
        if (dto == null || dto.getOrigin() == null || dto.getDestination() == null) {
            throw new IllegalArgumentException(FlightConstants.ERR_ORIGIN_DEST_REQUIRED);
        }

        LocalDate today = LocalDate.now();
        LocalDate date = dto.getDate() != null ? dto.getDate() : today;

        if (date.isBefore(today)) {
            throw new IllegalArgumentException(FlightConstants.ERR_DATE_IN_PAST);
        }

        String dep = dto.getOrigin();
        String arr = dto.getDestination();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return flightRepository
                .findByOriginAndDestinationAndDepartureAtBetween(dep, arr, start, end);
    }
}