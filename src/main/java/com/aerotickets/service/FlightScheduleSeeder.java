package com.aerotickets.service;

import com.aerotickets.constants.FlightConstants;
import com.aerotickets.constants.FlightSeedConstants;
import com.aerotickets.entity.Flight;
import com.aerotickets.repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
public class FlightScheduleSeeder implements CommandLineRunner {

    private final FlightRepository flightRepository;

    public FlightScheduleSeeder(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public void run(String... args) {
        List<Flight> existing = flightRepository.findAll();
        if (existing.isEmpty()) {
            return;
        }

        Map<RouteKey, RouteStats> statsByRoute = buildStats(existing);

        LocalDate today = LocalDate.now(FlightSeedConstants.SEED_ZONE_ID);
        LocalDate maxExistingDate = existing.stream()
                .map(Flight::getDepartureAt)
                .map(LocalDateTime::toLocalDate)
                .max(LocalDate::compareTo)
                .orElse(today);

        LocalDate fromDate = maxExistingDate.plusDays(1);
        if (fromDate.isBefore(today)) {
            fromDate = today;
        }

        LocalDate toDate = today.plusDays(FlightSeedConstants.SEED_DAYS_AHEAD);
        if (fromDate.isAfter(toDate)) {
            return;
        }

        List<Flight> toSave = new ArrayList<>();

        for (Map.Entry<RouteKey, RouteStats> entry : statsByRoute.entrySet()) {
            RouteKey route = entry.getKey();
            RouteStats stats = entry.getValue();
            LocalDate date = fromDate;
            while (!date.isAfter(toDate)) {
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = start.plusDays(1);

                List<Flight> already = flightRepository
                        .findByOriginAndDestinationAndDepartureAtBetween(
                                route.origin, route.destination, start, end
                        );

                if (already.isEmpty()) {
                    List<Flight> generated = generateForDay(route, stats, date);
                    toSave.addAll(generated);
                }

                date = date.plusDays(1);
            }
        }

        if (!toSave.isEmpty()) {
            flightRepository.saveAll(toSave);
        }
    }

    private Map<RouteKey, RouteStats> buildStats(List<Flight> flights) {
        Map<RouteKey, RouteStats> stats = new HashMap<>();
        for (Flight f : flights) {
            if (f.getOrigin() == null || f.getDestination() == null) {
                continue;
            }
            RouteKey key = new RouteKey(f.getOrigin(), f.getDestination());
            RouteStats s = stats.computeIfAbsent(key, k -> new RouteStats());
            s.add(f);
        }
        stats.values().forEach(RouteStats::finalizeStats);
        return stats;
    }

    private List<Flight> generateForDay(RouteKey route, RouteStats stats, LocalDate date) {
        List<Flight> result = new ArrayList<>();

        int flightsPerDay = FlightSeedConstants.FLIGHTS_PER_ROUTE_PER_DAY;
        int[] hours = FlightSeedConstants.DEFAULT_DEPARTURE_HOURS;
        int count = Math.min(flightsPerDay, hours.length);

        for (int i = 0; i < count; i++) {
            int hour = hours[i];
            LocalDateTime dep = LocalDateTime.of(date, LocalTime.of(hour, 0));
            LocalDateTime arr = dep.plusMinutes(stats.durationMinutes);

            Flight f = new Flight();
            f.setAirline(stats.airline);
            f.setOrigin(route.origin);
            f.setDestination(route.destination);
            f.setDepartureAt(dep);
            f.setArriveAt(arr);
            f.setTotalSeats(stats.totalSeats);
            f.setPrice(stats.price);

            result.add(f);
        }

        return result;
    }

    private static final class RouteKey {
        private final String origin;
        private final String destination;

        private RouteKey(String origin, String destination) {
            this.origin = origin;
            this.destination = destination;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RouteKey)) {
                return false;
            }
            RouteKey that = (RouteKey) o;
            return Objects.equals(origin, that.origin)
                    && Objects.equals(destination, that.destination);
        }

        @Override
        public int hashCode() {
            return Objects.hash(origin, destination);
        }
    }

    private static final class RouteStats {
        private final Map<String, Integer> airlineCount = new HashMap<>();
        private long totalDurationMinutes = 0;
        private int durationSamples = 0;
        private int totalSeatsSum = 0;
        private int seatsSamples = 0;
        private BigDecimal priceSum = BigDecimal.ZERO;
        private int priceSamples = 0;

        private String airline;
        private int durationMinutes;
        private int totalSeats;
        private BigDecimal price;

        private void add(Flight f) {
            if (f.getAirline() != null) {
                airlineCount.merge(f.getAirline(), 1, Integer::sum);
            }

            if (f.getDepartureAt() != null && f.getArriveAt() != null) {
                long mins = Duration.between(f.getDepartureAt(), f.getArriveAt()).toMinutes();
                if (mins > 0) {
                    totalDurationMinutes += mins;
                    durationSamples++;
                }
            }

            if (f.getTotalSeats() != null && f.getTotalSeats() > 0) {
                totalSeatsSum += f.getTotalSeats();
                seatsSamples++;
            }

            if (f.getPrice() != null) {
                priceSum = priceSum.add(f.getPrice());
                priceSamples++;
            }
        }

        private void finalizeStats() {
            this.airline = airlineCount.entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .orElse(FlightConstants.DEFAULT_AIRLINE_NAME);

            if (durationSamples > 0) {
                this.durationMinutes = (int) (totalDurationMinutes / durationSamples);
            } else {
                this.durationMinutes = FlightConstants.DEFAULT_DURATION_HOURS * 60;
            }

            if (seatsSamples > 0) {
                this.totalSeats = totalSeatsSum / seatsSamples;
            } else {
                this.totalSeats = FlightConstants.DEFAULT_TOTAL_SEATS;
            }

            if (priceSamples > 0) {
                this.price = priceSum.divide(
                        BigDecimal.valueOf(priceSamples),
                        RoundingMode.HALF_UP
                );
            } else {
                this.price = FlightConstants.DEFAULT_PRICE;
            }
        }
    }
}