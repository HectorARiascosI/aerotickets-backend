package com.aerotickets.service;

import com.aerotickets.constants.FlightConstants;
import com.aerotickets.constants.FlightSeedConstants;
import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.entity.Flight;
import com.aerotickets.entity.RouteProfileCo;
import com.aerotickets.repository.FlightRepository;
import com.aerotickets.repository.RouteProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final RouteProfileRepository routeProfileRepository;

    public FlightService(FlightRepository flightRepository,
                         RouteProfileRepository routeProfileRepository) {
        this.flightRepository = flightRepository;
        this.routeProfileRepository = routeProfileRepository;
    }

    @Transactional
    public Flight create(Flight f) {
        LocalDateTime now = LocalDateTime.now(FlightSeedConstants.SEED_ZONE_ID);
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

    @Transactional
    public List<Flight> searchOrSimulate(FlightSearchDTO dto) {
        if (dto == null || dto.getOrigin() == null || dto.getDestination() == null) {
            throw new IllegalArgumentException(FlightConstants.ERR_ORIGIN_DEST_REQUIRED);
        }

        ZoneId zone = FlightSeedConstants.SEED_ZONE_ID;
        LocalDate today = LocalDate.now(zone);
        LocalDateTime now = LocalDateTime.now(zone);

        LocalDate date = dto.getDate() != null ? dto.getDate() : today;

        if (date.isBefore(today)) {
            throw new IllegalArgumentException(FlightConstants.ERR_DATE_IN_PAST);
        }

        String origin = dto.getOrigin();
        String destination = dto.getDestination();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<Flight> flights = flightRepository
                .findByOriginAndDestinationAndDepartureAtBetween(origin, destination, start, end);

        if (date.isEqual(today)) {
            flights = filterFromNow(flights, now);
        }

        if (!flights.isEmpty()) {
            return flights;
        }

        List<Flight> generated = generateAndPersistDailyFlights(origin, destination, date, today, now);

        if (date.isEqual(today)) {
            generated = filterFromNow(generated, now);
        }

        return generated;
    }

    private List<Flight> filterFromNow(List<Flight> flights, LocalDateTime now) {
        List<Flight> result = new ArrayList<>();
        for (Flight f : flights) {
            if (f.getDepartureAt() != null && !f.getDepartureAt().isBefore(now)) {
                result.add(f);
            }
        }
        return result;
    }

    private List<Flight> generateAndPersistDailyFlights(String origin,
                                                        String destination,
                                                        LocalDate date,
                                                        LocalDate today,
                                                        LocalDateTime now) {
        RouteProfileCo profile = routeProfileRepository
                .findByOriginIataAndDestinationIata(origin, destination)
                .orElse(null);

        int typicalMinutes;
        if (profile != null && profile.getTypicalMinutes() != null && profile.getTypicalMinutes() > 0) {
            typicalMinutes = profile.getTypicalMinutes();
        } else {
            typicalMinutes = FlightConstants.DEFAULT_DURATION_HOURS * 60;
        }

        int[] hours = FlightSeedConstants.DEFAULT_DEPARTURE_HOURS;
        int flightsPerDay = FlightSeedConstants.FLIGHTS_PER_ROUTE_PER_DAY;
        int count = Math.min(flightsPerDay, hours.length);

        List<Flight> toSave = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int hour = hours[i];

            LocalDateTime departureAt = LocalDateTime.of(date, LocalTime.of(hour, 0));

            if (date.isEqual(today) && departureAt.isBefore(now)) {
                continue;
            }

            LocalDateTime arriveAt = departureAt.plusMinutes(typicalMinutes);

            BigDecimal basePrice = FlightSeedConstants.PRICE_PER_MINUTE
                    .multiply(BigDecimal.valueOf(typicalMinutes));
            if (basePrice.compareTo(FlightSeedConstants.MIN_BASE_PRICE) < 0) {
                basePrice = FlightSeedConstants.MIN_BASE_PRICE;
            }

            Flight f = new Flight();
            f.setAirline(FlightConstants.DEFAULT_AIRLINE_NAME);
            f.setOrigin(origin);
            f.setDestination(destination);
            f.setDepartureAt(departureAt);
            f.setArriveAt(arriveAt);
            f.setTotalSeats(FlightConstants.DEFAULT_TOTAL_SEATS);
            f.setPrice(basePrice);

            toSave.add(f);
        }

        if (toSave.isEmpty()) {
            return List.of();
        }

        return flightRepository.saveAll(toSave);
    }
}