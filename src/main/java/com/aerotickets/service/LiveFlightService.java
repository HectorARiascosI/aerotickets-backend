package com.aerotickets.service;

import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.model.LiveFlight;
import com.aerotickets.sim.AirportCatalogCO;
import com.aerotickets.util.IataResolver;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Servicio de búsqueda de vuelos en tiempo real (sim por ahora).
 * - Convierte nombres humanos → IATA
 * - Autocomplete basado en catálogo nacional
 * - Delegación de simulación a FlightSimulatorService
 */
@Service
public class LiveFlightService {

    private final FlightSimulatorService simulator;

    public LiveFlightService(FlightSimulatorService simulator) {
        this.simulator = simulator;
    }

    public List<LiveFlight> searchLive(String originRaw, String destinationRaw, String dateIso, String ignored) {
        String origin = smartToIata(originRaw);
        String destination = smartToIata(destinationRaw);
        if (origin == null || destination == null || origin.equalsIgnoreCase(destination)) return List.of();

        FlightSearchDTO dto = new FlightSearchDTO();
        dto.setOrigin(origin);
        dto.setDestination(destination);
        if (dateIso != null && !dateIso.isBlank()) {
            try { dto.setDate(LocalDate.parse(dateIso)); } catch (Exception ignored2) {}
        }
        return simulator.search(dto);
    }

    public List<Map<String, Object>> autocompleteAirports(String query) {
        if (query == null || query.isBlank()) return List.of();
        query = IataResolver.normalize(query);

        List<Map<String, Object>> results = new ArrayList<>();
        for (String iata : AirportCatalogCO.keys()) {
            AirportCatalogCO.Airport info = AirportCatalogCO.get(iata);
            if (matches(info, query)) {
                Map<String, Object> item = new HashMap<>();
                item.put("iata", info.iata);
                item.put("city", info.city);
                item.put("airport", info.name);
                item.put("terrain", info.terrain);
                item.put("runway_m", info.runwayLenM);
                item.put("elevation_ft", info.elevationFt);
                item.put("allowed_families", info.allowedFamilies);
                results.add(item);
            }
        }
        results.sort(Comparator.comparing(m -> ((String) m.get("city"))));
        return results;
    }

    private boolean matches(AirportCatalogCO.Airport info, String query) {
        String city = IataResolver.normalize(info.city);
        String name = IataResolver.normalize(info.name);
        String iata = info.iata.toLowerCase(Locale.ROOT);
        String terrain = IataResolver.normalize(info.terrain);
        return iata.contains(query) || city.contains(query) || name.contains(query) || terrain.contains(query);
    }

    private String smartToIata(String input) {
        if (input == null || input.isBlank()) return null;
        String resolved = IataResolver.toIata(input);
        if (resolved != null) return resolved;

        String normalized = IataResolver.normalize(input);
        String best = null; int bestScore = Integer.MAX_VALUE;
        for (String iata : AirportCatalogCO.keys()) {
            var a = AirportCatalogCO.get(iata);
            String city = IataResolver.normalize(a.city);
            String name = IataResolver.normalize(a.name);
            if (normalized.equals(iata.toLowerCase(Locale.ROOT)) || normalized.equals(city) || normalized.equals(name)) {
                return iata;
            }
            int score = levenshtein(normalized, city);
            if (score < bestScore) { bestScore = score; best = iata; }
        }
        return bestScore <= Math.max(2, normalized.length() / 2) ? best : null;
    }

    private int levenshtein(String a, String b) {
        int n = a.length(), m = b.length();
        if (n == 0) return m; if (m == 0) return n;
        int[] prev = new int[m + 1], cur = new int[m + 1];
        for (int j = 0; j <= m; j++) prev[j] = j;
        for (int i = 1; i <= n; i++) {
            cur[0] = i;
            for (int j = 1; j <= m; j++) {
                int cost = (a.charAt(i-1) == b.charAt(j-1)) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j-1]+1, prev[j]+1), prev[j-1]+cost);
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[m];
    }
}