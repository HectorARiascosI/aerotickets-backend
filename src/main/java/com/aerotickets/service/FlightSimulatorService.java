package com.aerotickets.service;

import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.model.LiveFlight;
import com.aerotickets.sim.AirportCatalogCO;

import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FlightSimulatorService {

    private static final ZoneId ZONE = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter ISO_LOCAL = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final Map<String, List<String>> AIRLINE_FLEET = Map.of(
        "Avianca", List.of("A320-200","A320neo","A319","A321"),
        "LATAM Colombia", List.of("A320-200","A320neo"),
        "Wingo", List.of("B737-800"),
        "Satena", List.of("ERJ-145","ATR 42-600"),
        "Clic", List.of("ATR 72-600")
    );

    private static final Map<String,String> AIRLINE_CODE = Map.of(
        "Avianca","AV","LATAM Colombia","LA","Wingo","P5","Satena","9R","Clic","VE"
    );

    private static final Map<String, Integer> TYPICAL_MINS = Map.ofEntries(
        Map.entry("BOG-MDE", 65), Map.entry("MDE-BOG", 65),
        Map.entry("BOG-CTG", 80), Map.entry("CTG-BOG", 80),
        Map.entry("BOG-BAQ", 85), Map.entry("BAQ-BOG", 85),
        Map.entry("BOG-SMR", 85), Map.entry("SMR-BOG", 85),
        Map.entry("BOG-CLO", 65), Map.entry("CLO-BOG", 65),
        Map.entry("BOG-PEI", 45), Map.entry("PEI-BOG", 45),
        Map.entry("BOG-AXM", 45), Map.entry("AXM-BOG", 45),
        Map.entry("BOG-BGA", 55), Map.entry("BGA-BOG", 55),
        Map.entry("BOG-CUC", 75), Map.entry("CUC-BOG", 75),
        Map.entry("MDE-CTG", 55), Map.entry("CTG-MDE", 55),
        Map.entry("MDE-SMR", 60), Map.entry("SMR-MDE", 60),
        Map.entry("MDE-BAQ", 60), Map.entry("BAQ-MDE", 60),
        Map.entry("CLO-CTG", 95), Map.entry("CTG-CLO", 95)
    );

    public List<LiveFlight> search(FlightSearchDTO dto) {
        String o = dto.getOrigin();
        String d = dto.getDestination();
        if (!AirportCatalogCO.isDomesticPair(o, d)) return List.of();

        LocalDate date = dto.getDate() != null ? dto.getDate() : LocalDate.now(ZONE);

        // # vuelos entre 3 y 7
        int count = ThreadLocalRandom.current().nextInt(3, 8);
        List<String> airlines = new ArrayList<>(AIRLINE_FLEET.keySet());

        int typical = TYPICAL_MINS.getOrDefault(o + "-" + d,
                ThreadLocalRandom.current().nextInt(60, 121));

        List<LiveFlight> out = new ArrayList<>();
        LocalTime first = LocalTime.of(6, 0);
        int step = Math.max(60, typical);

        for (int i = 0; i < count; i++) {
            String airline = airlines.get(ThreadLocalRandom.current().nextInt(airlines.size()));
            String code = AIRLINE_CODE.getOrDefault(airline, "XX");
            List<String> fleet = AIRLINE_FLEET.get(airline);
            String aircraft = fleet.get(ThreadLocalRandom.current().nextInt(fleet.size()));
            int seats = seatsFor(aircraft);

            LocalDateTime dep = LocalDateTime.of(date, first.plusMinutes((long) i * step));
            dep = dep.plusMinutes(ThreadLocalRandom.current().nextLong(-10, 16));
            LocalDateTime arr = dep.plusMinutes(typical + ThreadLocalRandom.current().nextInt(-5, 11));

            String depS = dep.format(ISO_LOCAL);
            String arrS = arr.format(ISO_LOCAL);

            String status = pickStatus();
            Integer delay = "DELAYED".equals(status) ? ThreadLocalRandom.current().nextInt(5, 41) : null;

            LiveFlight lf = new LiveFlight();
            lf.setProvider("sim");
            lf.setAirline(airline);
            lf.setAirlineCode(code);
            lf.setFlightNumber(code + ThreadLocalRandom.current().nextInt(10, 9999));
            lf.setOriginIata(o);
            lf.setDestinationIata(d);
            lf.setDepartureAt(depS);
            lf.setArrivalAt(arrS);
            lf.setStatus(status);
            lf.setDelayMinutes(delay);
            lf.setAircraftType(aircraft);
            lf.setTerminal(terminalFor(o));
            lf.setGate(randomGate());
            lf.setBaggageBelt(null);
            lf.setTotalSeats(seats);
            lf.setOccupiedSeats((int) (seats * (0.6 + ThreadLocalRandom.current().nextDouble() * 0.35)));
            lf.setCargoKg(ThreadLocalRandom.current().nextInt(500, 4000));
            // Boarding (20–35 min antes)
            lf.setBoardingStartAt(dep.minusMinutes(ThreadLocalRandom.current().nextInt(25, 36)).format(ISO_LOCAL));
            lf.setBoardingEndAt(dep.minusMinutes(10).format(ISO_LOCAL));
            out.add(lf);
        }

        out.sort(Comparator.comparing(LiveFlight::getDepartureAt));
        return out;
    }

    private int seatsFor(String aircraft) {
        return switch (aircraft) {
            case "B737-800" -> 186;
            case "A321" -> 220;
            case "A320neo" -> 186;
            case "A320-200" -> 180;
            case "A319" -> 132;
            case "ATR 72-600" -> 70;
            case "ATR 42-600" -> 48;
            case "ERJ-145" -> 50;
            default -> 160;
        };
    }

    private String terminalFor(String iata) {
        if ("BOG".equals(iata)) return "T1";
        if (Set.of("MDE","CTG","BAQ","SMR").contains(iata)) return "T2";
        return null;
    }

    private String randomGate() {
        char letter = (char) ('A' + ThreadLocalRandom.current().nextInt(0, 3));
        int num = ThreadLocalRandom.current().nextInt(1, 30);
        return letter + String.valueOf(num);
    }

    private String pickStatus() {
        int r = ThreadLocalRandom.current().nextInt(100);
        if (r < 70) return "SCHEDULED";
        if (r < 85) return "EN-ROUTE";
        if (r < 93) return "DELAYED";
        if (r < 98) return "LANDED";
        return "CANCELLED";
    }
}