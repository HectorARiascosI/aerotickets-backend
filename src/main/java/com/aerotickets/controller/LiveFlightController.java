package com.aerotickets.controller;

import com.aerotickets.constants.LiveFlightConstants;
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
@RequestMapping(LiveFlightConstants.BASE_PATH)
public class LiveFlightController {

    private final LiveFlightService liveService;
    private final SimulationRegistry registry;

    public LiveFlightController(LiveFlightService liveService, SimulationRegistry registry) {
        this.liveService = liveService;
        this.registry = registry;
    }

    @CrossOrigin(
        origins = {
            LiveFlightConstants.CORS_ORIGIN_LOCAL,
            LiveFlightConstants.CORS_ORIGIN_VERCEL_MAIN,
            LiveFlightConstants.CORS_ORIGIN_VERCEL_WILDCARD
        },
        allowCredentials = "true"
    )
    @GetMapping(value = LiveFlightConstants.STREAM_PATH, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = registry.subscribe();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        
        ScheduledFuture<?> pingTask = exec.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(LiveFlightConstants.SSE_EVENT_PING_NAME)
                        .data(LiveFlightConstants.SSE_EVENT_PING_DATA));
            } catch (Exception e) {
                try {
                    emitter.complete();
                } catch (Exception ignored) {
                }
                exec.shutdownNow();
            }
        }, LiveFlightConstants.PING_INITIAL_DELAY_SECONDS, LiveFlightConstants.PING_PERIOD_SECONDS, TimeUnit.SECONDS);

        emitter.onCompletion(() -> {
            pingTask.cancel(true);
            exec.shutdownNow();
        });
        emitter.onTimeout(() -> {
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
            pingTask.cancel(true);
            exec.shutdownNow();
        });
        emitter.onError(ex -> {
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
            pingTask.cancel(true);
            exec.shutdownNow();
        });
        
        return emitter;
    }

    @PostMapping(value = LiveFlightConstants.FLIGHTS_SEARCH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value = LiveFlightConstants.AIRPORTS_SEARCH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> airports(
            @RequestParam(LiveFlightConstants.PARAM_QUERY) String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(liveService.autocompleteAirports(query));
    }
}