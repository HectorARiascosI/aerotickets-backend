package com.aerotickets.controller;

import com.aerotickets.constants.ReservationConstants;
import com.aerotickets.dto.ReservationRequestDTO;
import com.aerotickets.dto.ReservationResponseDTO;
import com.aerotickets.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ReservationConstants.BASE_PATH)
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> create(Authentication auth,
                                                         @Valid @RequestBody ReservationRequestDTO dto) {
        String email = (auth != null) ? auth.getName() : null;
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(service.create(email, dto));
    }

    @GetMapping(ReservationConstants.MY_PATH)
    public ResponseEntity<List<ReservationResponseDTO>> myReservations(Authentication auth) {
        String email = (auth != null) ? auth.getName() : null;
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(service.listMine(email));
    }

    @GetMapping(ReservationConstants.ME_PATH)
    public ResponseEntity<List<ReservationResponseDTO>> myReservationsAlias(Authentication auth) {
        String email = (auth != null) ? auth.getName() : null;
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(service.listMine(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(Authentication auth, @PathVariable Long id) {
        String email = (auth != null) ? auth.getName() : null;
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        service.cancel(email, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/occupied-seats/{flightId}")
    public ResponseEntity<List<String>> getOccupiedSeats(@PathVariable Long flightId) {
        return ResponseEntity.ok(service.getOccupiedSeats(flightId));
    }
}