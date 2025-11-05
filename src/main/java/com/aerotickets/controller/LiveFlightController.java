package com.aerotickets.controller;

import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.model.LiveFlight;
import com.aerotickets.service.LiveFlightService;
import com.aerotickets.service.SimulationRegistry;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * Controlador principal para gestionar vuelos en tiempo real.
 * Exposición de endpoints SSE (stream) y consultas HTTP JSON.
 */
@RestController
@RequestMapping("/live") // <<-- OJO: el context-path /api viene del application.yml
public class LiveFlightController {

    private final LiveFlightService liveService;
    private final SimulationRegistry registry;

    public LiveFlightController(LiveFlightService liveService, SimulationRegistry registry) {
        this.liveService = liveService;
        this.registry = registry;
    }

    /** SSE: text/event-stream */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return registry.subscribe();
    }

    /** Búsqueda de vuelos (en vivo + simulados) */
    @PostMapping(value = "/flights/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LiveFlight>> search(@Valid @RequestBody FlightSearchDTO dto) {
        if (dto.getOrigin() == null || dto.getDestination() == null
                || dto.getOrigin().isBlank() || dto.getDestination().isBlank()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        List<LiveFlight> results = liveService.searchLive(
                dto.getOrigin(),
                dto.getDestination(),
                dto.getDate() != null ? dto.getDate().toString() : null,
                null
        );
        return ResponseEntity.ok(results);
    }

    /** Autocompletado de aeropuertos */
    @GetMapping(value = "/airports/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> airports(@RequestParam("query") String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        List<Map<String, Object>> airports = liveService.autocompleteAirports(query);
        return ResponseEntity.ok(airports);
    }
}