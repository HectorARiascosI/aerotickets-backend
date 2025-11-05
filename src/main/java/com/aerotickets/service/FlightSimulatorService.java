package com.aerotickets.service;

import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.model.LiveFlight;
import com.aerotickets.sim.*;
import com.aerotickets.util.IataResolver;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FlightSimulatorService {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Aerolíneas activas en CO (sin Ultra/Viva)
    private static final Map<String, String> CARRIERS = Map.of(
            "AV", "Avianca",
            "LA", "LATAM Airlines",
            "9R", "SATENA",
            "VE", "CLIC (Regional)",
            "P5", "Wingo"
    );

    private final SimulationRegistry registry;

    public FlightSimulatorService(SimulationRegistry registry) {
        this.registry = registry;
    }

    public List<LiveFlight> search(FlightSearchDTO dto) {
        String dep = IataResolver.toIata(dto.getOrigin());
        String arr = IataResolver.toIata(dto.getDestination());
        if (dep == null || arr == null || dep.equalsIgnoreCase(arr)) return List.of();

        var aDep = AirportCatalogCO.get(dep);
        var aArr = AirportCatalogCO.get(arr);
        if (aDep == null || aArr == null) return List.of();

        LocalDate date = dto.getDate() != null ? dto.getDate() : LocalDate.now();
        int seed = Objects.hash(dep, arr, date);
        Random rnd = new Random(seed);

        ZoneId tz = ZoneId.of("America/Bogota");

        // Clima del día
        var wxDepDay = WeatherProfileCatalog.forAirportDay(aDep, date, rnd);
        var wxArrDay = WeatherProfileCatalog.forAirportDay(aArr, date, rnd);

        int flights = 5 + rnd.nextInt(4); // 5..8 vuelos simulados viables
        List<LiveFlight> out = new ArrayList<>(flights);

        int tries = 0;
        while (out.size() < flights && tries < flights * 6) {
            tries++;

            // Elige aerolínea válida para ambos aeropuertos
            String carrier = pickCarrier(aDep, aArr, rnd);
            if (carrier == null) continue;

            // Elige tipo de aeronave permitido (en ambos)
            String family = pickFamily(aDep, aArr, rnd);
            if (family == null) continue;

            // Programa un slot razonable
            int startHour = 6 + rnd.nextInt(14); // 06..19
            int minute = (rnd.nextInt(5) * 10);  // múltiplos de 10
            LocalDateTime schedDep = LocalDateTime.of(date, LocalTime.of(startHour, minute));
            LocalDateTime schedArr = schedDep.plusMinutes(estimateBlock(aDep, aArr, family, rnd));

            // Temperatura simple por altitud (más alto => más frío)
            int tempDep = Math.max(8, 24 - (aDep.elevationFt / 1000)); // conservador
            int tempArr = Math.max(8, 24 - (aArr.elevationFt / 1000));

            // Decide si es operable en salida y llegada
            var decDep = RestrictionsEngine.canOperate(aDep, family, tempDep, wxDepDay, schedDep.toLocalTime());
            if (!decDep.allowed) continue;
            var decArr = RestrictionsEngine.canOperate(aArr, family, tempArr, wxArrDay, schedArr.toLocalTime());
            if (!decArr.allowed) continue;

            // Retrasos por clima/congestión
            int delay = 0;
            delay += wxPenalty(wxDepDay);
            delay += wxPenalty(wxArrDay);
            delay += ScheduleRules.congestionDelayMin(aDep, schedDep.toLocalTime());
            delay += ScheduleRules.congestionDelayMin(aArr, schedArr.toLocalTime());
            if (delay > 40) delay = 40; // top-out conservador

            LocalDateTime depFinal = schedDep.plusMinutes(delay);
            LocalDateTime arrFinal = schedArr.plusMinutes(delay);
            String status = nowStatus(depFinal, arrFinal, tz, delay);

            String code = carrier + (100 + rnd.nextInt(900));
            LiveFlight lf = new LiveFlight(
                    "sim-co",
                    CARRIERS.get(carrier),
                    code,
                    dep, arr,
                    depFinal.format(ISO),
                    arrFinal.format(ISO),
                    status
            );
            lf.setAirlineCode(carrier);
            lf.setAircraftType(family);
            lf.setTerminal(rnd.nextBoolean() ? "T1" : "T2");
            lf.setGate(gateFor(aDep, rnd));
            lf.setDelayMinutes(delay > 0 ? delay : null);
            lf.setTotalSeats(capacityFor(family));
            lf.setOccupiedSeats((int)(lf.getTotalSeats() * (0.65 + rnd.nextDouble()*0.25)));
            lf.setCargoKg(Math.max(0, lf.getOccupiedSeats()*12 + rnd.nextInt(1200)));

            out.add(lf);
        }

        out.sort(Comparator.comparing(LiveFlight::getDepartureAt));
        registry.putAll(out);
        return out;
    }

    private String pickCarrier(AirportCatalogCO.Airport aDep, AirportCatalogCO.Airport aArr, Random rnd) {
        var inter = new ArrayList<String>();
        for (String c : aDep.allowedCarriers) if (aArr.allowedCarriers.contains(c)) inter.add(c);
        if (inter.isEmpty()) return null;
        return inter.get(rnd.nextInt(inter.size()));
    }

    private String pickFamily(AirportCatalogCO.Airport aDep, AirportCatalogCO.Airport aArr, Random rnd) {
        var inter = new ArrayList<String>();
        for (String f : aDep.allowedFamilies) if (aArr.allowedFamilies.contains(f)) inter.add(f);
        if (inter.isEmpty()) return null;
        return inter.get(rnd.nextInt(inter.size()));
    }

    private int estimateBlock(AirportCatalogCO.Airport aDep, AirportCatalogCO.Airport aArr, String family, Random rnd) {
        // Distancia aproximada por coords (muy simple)
        double dkm = haversineKm(aDep.lat, aDep.lon, aArr.lat, aArr.lon);
        double cruiseKmh = switch (family) {
            case "ATR 72-600" -> 480;
            case "E190","E195" -> 780;
            default -> 830;
        };
        double hours = dkm / Math.max(350, cruiseKmh);
        int taxi = 15 + rnd.nextInt(10);
        int jitter = rnd.nextInt(7) - 3;
        return Math.max(40, (int)Math.round(hours*60) + taxi + jitter);
    }

 

    private int wxPenalty(WeatherProfileCatalog.Wx wx) {
        int d = 0;
        // Suavizamos: la mayoría de vuelos no deberían pasar de 10–15 min por clima
        if (wx.heavyRain) d += 6 + (int)(Math.random()*6); // antes 10–20
        if (wx.fog)      d += 4 + (int)(Math.random()*4); // antes 8–14
        if (wx.crosswindKts >= 24) d += 4;                 // antes 6 con umbral 22
        return d;
    }


    private String nowStatus(LocalDateTime dep, LocalDateTime arr, ZoneId tz, int delay) {
        LocalDateTime now = LocalDateTime.now(tz);
        if (now.isBefore(dep.minusMinutes(60))) return "SCHEDULED";
        if (!now.isAfter(dep) && now.isAfter(dep.minusMinutes(60))) return delay > 12 ? "DELAYED" : "BOARDING";
        if (now.isAfter(dep) && now.isBefore(arr)) return delay > 12 ? "DELAYED" : "EN-ROUTE";
        return "LANDED";
    }

    private String gateFor(AirportCatalogCO.Airport ap, Random rnd) {
        char base = switch (ap.city) {
            case "Bogotá" -> 'A';
            case "Rionegro/Medellín" -> 'B';
            case "Cali" -> 'C';
            case "Cartagena" -> 'D';
            case "Pasto" -> 'E';
            default -> 'F';
        };
        return base + String.valueOf(1 + rnd.nextInt(20));
    }

    private int capacityFor(String family) {
        return switch (family) {
            case "ATR 72-600" -> 70;
            case "E190" -> 100;
            case "E195" -> 118;
            case "A319" -> 132;
            case "A320","A320neo" -> 174;
            case "A321" -> 220;
            case "B737-800","B737 MAX 8" -> 186;
            default -> 150;
        };
    }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
                        Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R*c;
    }
}