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
import java.util.concurrent.*;

@RestController
@RequestMapping("/live") // context-path /api viene de application.yml
public class LiveFlightController {

    private final LiveFlightService liveService;
    private final SimulationRegistry registry;

    public LiveFlightController(LiveFlightService liveService, SimulationRegistry registry) {
        this.liveService = liveService;
        this.registry = registry;
    }

    @CrossOrigin(
        origins = {
            "http://localhost:5173",
            "https://aerotickets-frontend.vercel.app",
            "https://*.vercel.app"
        },
        allowCredentials = "true"
    )
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = registry.subscribe();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            try { emitter.send(SseEmitter.event().name("ping").data("♥")); }
            catch (Exception e) { emitter.complete(); exec.shutdownNow(); }
        }, 20, 20, TimeUnit.SECONDS);

        emitter.onCompletion(exec::shutdownNow);
        emitter.onTimeout(() -> { emitter.complete(); exec.shutdownNow(); });
        return emitter;
    }

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

    @GetMapping(value = "/airports/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> airports(@RequestParam("query") String query) {
        if (query == null || query.isBlank()) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(liveService.autocompleteAirports(query));
    }
}