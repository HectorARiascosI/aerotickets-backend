package com.aerotickets.service;

import com.aerotickets.constants.LiveFlightConstants;
import com.aerotickets.constants.LiveFlightStatus;
import com.aerotickets.model.LiveFlight;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.aerotickets.constants.LiveFlightConstants.ZONE_ID_BOGOTA;

@Component
public class SimulationRegistry {

    private final Map<String, LiveFlight> live = new ConcurrentHashMap<>();
    private final List<SseEmitter> subscribers = new CopyOnWriteArrayList<>();

    public void putAll(List<LiveFlight> flights) {
        for (LiveFlight f : flights) {
            live.put(f.getFlightNumber(), f);
        }
        broadcastSnapshot();
    }

    public List<LiveFlight> list() {
        return new ArrayList<>(live.values());
    }

    public Optional<LiveFlight> get(String flightNumber) {
        return Optional.ofNullable(live.get(flightNumber));
    }

    public void update(LiveFlight f) {
        live.put(f.getFlightNumber(), f);
    }

    public void clear() {
        live.clear();
        broadcastSnapshot();
    }

    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    public void tick() {
        ZoneId tz = ZoneId.of(ZONE_ID_BOGOTA);
        LocalDateTime now = LocalDateTime.now(tz);

        for (LiveFlight f : live.values()) {
            try {
                LocalDateTime dep = LocalDateTime.parse(f.getDepartureAt());
                LocalDateTime arr = LocalDateTime.parse(f.getArrivalAt());

                LiveFlightStatus currentStatus = parseStatus(f.getStatus());
                if (currentStatus == LiveFlightStatus.CANCELLED
                        || currentStatus == LiveFlightStatus.DIVERTED) {
                    continue;
                }

                boolean delayed = f.getDelayMinutes() != null && f.getDelayMinutes() > 10;

                LiveFlightStatus newStatus;
                if (now.isBefore(dep.minusMinutes(60))) {
                    newStatus = LiveFlightStatus.SCHEDULED;
                } else if (!now.isAfter(dep) && now.isAfter(dep.minusMinutes(60))) {
                    newStatus = delayed ? LiveFlightStatus.DELAYED : LiveFlightStatus.BOARDING;
                } else if (now.isAfter(dep) && now.isBefore(arr)) {
                    newStatus = delayed ? LiveFlightStatus.DELAYED : LiveFlightStatus.EN_ROUTE;
                } else {
                    newStatus = LiveFlightStatus.LANDED;
                }

                f.setStatus(newStatus.name());

            } catch (Exception ignored) {
            }
        }
        broadcastSnapshot();
    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        subscribers.add(emitter);

        emitter.onCompletion(() -> subscribers.remove(emitter));
        emitter.onTimeout(() -> {
            subscribers.remove(emitter);
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        });
        emitter.onError(ex -> {
            subscribers.remove(emitter);
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        });

        try {
            emitter.send(SseEmitter.event()
                    .name("snapshot")
                    .data(list(), MediaType.APPLICATION_JSON));
            emitter.send(SseEmitter.event()
                    .name(LiveFlightConstants.SSE_EVENT_PING_NAME)
                    .data(LiveFlightConstants.SSE_EVENT_PING_DATA));
        } catch (IOException | IllegalStateException e) {
            subscribers.remove(emitter);
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        }
        return emitter;
    }

    private void broadcastSnapshot() {
        List<LiveFlight> snapshot = list();
        for (SseEmitter em : new ArrayList<>(subscribers)) {
            try {
                em.send(SseEmitter.event()
                        .name("snapshot")
                        .data(snapshot, MediaType.APPLICATION_JSON));
            } catch (IOException | IllegalStateException e) {
                // Remove emitter if it's already completed or has an error
                subscribers.remove(em);
                try {
                    em.complete();
                } catch (Exception ignored) {
                    // Emitter might already be completed
                }
            }
        }
    }

    private LiveFlightStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return LiveFlightStatus.SCHEDULED;
        }
        try {
            return LiveFlightStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            return LiveFlightStatus.SCHEDULED;
        }
    }
}