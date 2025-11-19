package com.aerotickets.service;

import com.aerotickets.constants.LiveFlightConstants;
import com.aerotickets.constants.LiveFlightStatus;
import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.entity.Airport;
import com.aerotickets.entity.Flight;
import com.aerotickets.entity.ReservationStatus;
import com.aerotickets.model.LiveFlight;
import com.aerotickets.repository.AirportRepository;
import com.aerotickets.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.aerotickets.constants.LiveFlightConstants.ISO_LOCAL_PATTERN;
import static com.aerotickets.constants.LiveFlightConstants.ZONE_ID_BOGOTA;

@Service
public class LiveFlightService {

    private static final ZoneId ZONE = ZoneId.of(ZONE_ID_BOGOTA);
    private static final DateTimeFormatter ISO_LOCAL = DateTimeFormatter.ofPattern(ISO_LOCAL_PATTERN);

    private final ReservationRepository reservationRepository;
    private final AirportRepository airportRepository;
    private final FlightService flightService;

    public LiveFlightService(ReservationRepository reservationRepository,
                             AirportRepository airportRepository,
                             FlightService flightService) {
        this.reservationRepository = reservationRepository;
        this.airportRepository = airportRepository;
        this.flightService = flightService;
    }

    public List<LiveFlight> searchLive(String originRaw,
                                       String destinationRaw,
                                       String dateIso,
                                       String ignored) {
        if (originRaw == null || destinationRaw == null) {
            return List.of();
        }

        String origin = originRaw.trim().toUpperCase(Locale.ROOT);
        String destination = destinationRaw.trim().toUpperCase(Locale.ROOT);

        if (origin.isBlank() || destination.isBlank() || origin.equals(destination)) {
            return List.of();
        }

        if (!airportRepository.existsById(origin) || !airportRepository.existsById(destination)) {
            return List.of();
        }

        LocalDate today = LocalDate.now(ZONE);
        LocalDate date;
        if (dateIso != null && !dateIso.isBlank()) {
            try {
                date = LocalDate.parse(dateIso);
            } catch (Exception e) {
                date = today;
            }
        } else {
            date = today;
        }

        if (date.isBefore(today)) {
            return List.of();
        }

        FlightSearchDTO dto = new FlightSearchDTO(origin, destination, date);

        List<Flight> flights = flightService.searchOrSimulate(dto);
        if (flights.isEmpty()) {
            return List.of();
        }

        return flights.stream()
                .map(this::toLiveFlight)
                .sorted(Comparator.comparing(LiveFlight::getDepartureAt))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> autocompleteAirports(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String normalizedQuery = normalize(query);
        List<Airport> airports = airportRepository.findAll();

        return airports.stream()
                .filter(a -> matchesAirport(a, normalizedQuery))
                .sorted(Comparator.comparing(Airport::getCity, String.CASE_INSENSITIVE_ORDER))
                .map(a -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("iata", a.getIata());
                    m.put("city", a.getCity());
                    m.put("airport", a.getName());
                    m.put("state", a.getState());
                    return m;
                })
                .collect(Collectors.toList());
    }

    private LiveFlight toLiveFlight(Flight f) {
        long occupied = reservationRepository
                .countByFlight_IdAndStatus(f.getId(), ReservationStatus.ACTIVE);

        int totalSeats = f.getTotalSeats() != null ? f.getTotalSeats() : 0;
        int occupiedSeats = (int) Math.min(occupied, totalSeats);

        LiveFlightStatus status = computeStatus(f);

        LiveFlight lf = new LiveFlight();
        lf.setProvider(LiveFlightConstants.PROVIDER_DB);
        lf.setAirline(f.getAirline());
        lf.setAirlineCode(LiveFlightConstants.FLIGHT_NUMBER_PREFIX);
        lf.setFlightNumber(LiveFlightConstants.FLIGHT_NUMBER_PREFIX + f.getId());
        lf.setOriginIata(f.getOrigin());
        lf.setDestinationIata(f.getDestination());
        lf.setDepartureAt(f.getDepartureAt().atZone(ZONE).format(ISO_LOCAL));
        lf.setArrivalAt(f.getArriveAt().atZone(ZONE).format(ISO_LOCAL));
        lf.setStatus(status.name());
        lf.setDelayMinutes(null);
        lf.setAircraftType(null);
        lf.setTerminal(null);
        lf.setGate(null);
        lf.setBaggageBelt(null);
        lf.setTotalSeats(totalSeats);
        lf.setOccupiedSeats(occupiedSeats);
        lf.setCargoKg(estimateCargoKg(totalSeats, occupiedSeats));
        lf.setBoardingStartAt(boardingStart(f).format(ISO_LOCAL));
        lf.setBoardingEndAt(boardingEnd(f).format(ISO_LOCAL));

        return lf;
    }

    private LiveFlightStatus computeStatus(Flight f) {
        ZonedDateTime now = ZonedDateTime.now(ZONE);
        ZonedDateTime dep = f.getDepartureAt().atZone(ZONE);
        ZonedDateTime arr = f.getArriveAt().atZone(ZONE);

        long minutesToDeparture = Duration.between(now, dep).toMinutes();
        long minutesSinceDeparture = Duration.between(dep, now).toMinutes();
        long minutesToArrival = Duration.between(now, arr).toMinutes();

        if (minutesToDeparture > 60) {
            return LiveFlightStatus.SCHEDULED;
        }
        if (minutesToDeparture <= 60 && minutesToDeparture >= 0) {
            return LiveFlightStatus.BOARDING;
        }
        if (minutesSinceDeparture >= 0 && minutesToArrival > 0) {
            return LiveFlightStatus.EN_ROUTE;
        }
        return LiveFlightStatus.LANDED;
    }

    private int estimateCargoKg(int totalSeats, int occupiedSeats) {
        int paxFactor = Math.max(occupiedSeats, 0);
        return LiveFlightConstants.CARGO_BASE_KG
                + paxFactor * LiveFlightConstants.CARGO_PER_PAX_KG;
    }

    private ZonedDateTime boardingStart(Flight f) {
        ZonedDateTime dep = f.getDepartureAt().atZone(ZONE);
        return dep.minusMinutes(LiveFlightConstants.BOARDING_START_MINUTES_BEFORE);
    }

    private ZonedDateTime boardingEnd(Flight f) {
        ZonedDateTime dep = f.getDepartureAt().atZone(ZONE);
        return dep.minusMinutes(LiveFlightConstants.BOARDING_END_MINUTES_BEFORE);
    }

    private boolean matchesAirport(Airport a, String normalizedQuery) {
        String iata = a.getIata() != null ? a.getIata().toLowerCase(Locale.ROOT) : "";
        String city = normalize(a.getCity());
        String name = normalize(a.getName());
        return iata.contains(normalizedQuery)
                || city.contains(normalizedQuery)
                || name.contains(normalizedQuery);
    }

    private String normalize(String s) {
        if (s == null) {
            return "";
        }
        String t = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
        return t.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", " ");
    }
}