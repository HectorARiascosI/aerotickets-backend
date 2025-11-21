package com.aerotickets.service;

import com.aerotickets.constants.ReservationServiceConstants;
import com.aerotickets.dto.ReservationRequestDTO;
import com.aerotickets.dto.ReservationResponseDTO;
import com.aerotickets.entity.Flight;
import com.aerotickets.entity.Reservation;
import com.aerotickets.entity.ReservationStatus;
import com.aerotickets.entity.User;
import com.aerotickets.exception.ConflictException;
import com.aerotickets.exception.NotFoundException;
import com.aerotickets.repository.FlightRepository;
import com.aerotickets.repository.ReservationRepository;
import com.aerotickets.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              FlightRepository flightRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponseDTO create(String userEmail, ReservationRequestDTO dto) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException(ReservationServiceConstants.ERR_USER_EMAIL_REQUIRED);
        }
        if (dto == null || dto.getFlightId() == null) {
            throw new IllegalArgumentException(ReservationServiceConstants.ERR_FLIGHT_ID_REQUIRED);
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException(ReservationServiceConstants.ERR_USER_NOT_FOUND));

        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new NotFoundException(ReservationServiceConstants.ERR_FLIGHT_NOT_FOUND));

        // Verificar si el usuario ya tiene CUALQUIER reserva para este vuelo (ACTIVA o CANCELADA)
        // Esto evita que se pueda comprar el mismo vuelo múltiples veces
        List<Reservation> userReservations = reservationRepository
                .findByUser_EmailAndFlight_Id(userEmail, flight.getId());
        
        if (!userReservations.isEmpty()) {
            // Si tiene alguna reserva ACTIVA, mensaje específico
            boolean hasActiveReservation = userReservations.stream()
                    .anyMatch(r -> r.getStatus() == ReservationStatus.ACTIVE);
            
            if (hasActiveReservation) {
                throw new ConflictException(
                    ReservationServiceConstants.ERR_USER_ALREADY_HAS_ACTIVE_RESERVATION
                );
            }
            
            // Si ya tiene reservas (aunque estén canceladas), no puede comprar el mismo vuelo de nuevo
            throw new ConflictException(
                "Ya has reservado este vuelo anteriormente. No puedes volver a comprarlo."
            );
        }

        long activeCount = reservationRepository
                .countByFlight_IdAndStatus(flight.getId(), ReservationStatus.ACTIVE);
        if (activeCount >= flight.getTotalSeats()) {
            throw new ConflictException(ReservationServiceConstants.ERR_NO_SEATS_AVAILABLE);
        }

        String assignedSeat;
        if (dto.getSeatNumber() != null && !dto.getSeatNumber().isBlank()) {
            // Validar formato del asiento (debe ser número + letra, ej: "1A", "12B")
            if (!dto.getSeatNumber().matches("^\\d+[A-Z]$")) {
                throw new IllegalArgumentException(
                    "Formato de asiento inválido. Debe ser número + letra (ej: 1A, 12B)"
                );
            }
            
            boolean seatTaken = reservationRepository.existsByFlight_IdAndSeatNumberAndStatus(
                    flight.getId(), dto.getSeatNumber(), ReservationStatus.ACTIVE
            );
            if (seatTaken) {
                throw new ConflictException(ReservationServiceConstants.ERR_SEAT_TAKEN);
            }
            assignedSeat = dto.getSeatNumber();
        } else {
            assignedSeat = assignSeatNumber(flight);
        }

        try {
            Reservation r = Reservation.builder()
                    .user(user)
                    .flight(flight)
                    .seatNumber(assignedSeat)
                    .status(ReservationStatus.ACTIVE)
                    .build();

            Reservation saved = reservationRepository.save(r);
            return toDto(saved);

        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    ReservationServiceConstants.ERR_ACTIVE_RESERVATION_OR_SEAT_TAKEN
            );
        }
    }

    private String assignSeatNumber(Flight flight) {
        int totalSeats = flight.getTotalSeats();
        List<Reservation> activeReservations = reservationRepository
                .findByFlight_IdAndStatusOrderBySeatNumberAsc(
                        flight.getId(), ReservationStatus.ACTIVE
                );

        // Crear un set con los asientos ocupados
        java.util.Set<String> takenSeats = new java.util.HashSet<>();
        for (Reservation r : activeReservations) {
            String seat = r.getSeatNumber();
            if (seat != null && !seat.isBlank()) {
                takenSeats.add(seat);
            }
        }

        // Generar asientos en formato fila + letra (1A, 1B, 1C, 2A, 2B, etc.)
        // Asumiendo 6 asientos por fila (A, B, C, D, E, F)
        String[] columns = {"A", "B", "C", "D", "E", "F"};
        int seatsPerRow = columns.length;
        int totalRows = (int) Math.ceil((double) totalSeats / seatsPerRow);

        for (int row = 1; row <= totalRows; row++) {
            for (String col : columns) {
                String seatId = row + col;
                if (!takenSeats.contains(seatId)) {
                    return seatId;
                }
            }
        }

        throw new ConflictException(ReservationServiceConstants.ERR_NO_SEATS_AVAILABLE);
    }

    @Transactional
    public void cancel(String userEmail, Long reservationId) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException(ReservationServiceConstants.ERR_USER_EMAIL_REQUIRED);
        }
        if (reservationId == null) {
            throw new IllegalArgumentException(ReservationServiceConstants.ERR_RESERVATION_ID_REQUIRED);
        }

        Reservation r = reservationRepository.findByIdAndUser_Email(reservationId, userEmail)
                .orElseThrow(() -> new NotFoundException(
                        ReservationServiceConstants.ERR_RESERVATION_NOT_FOUND));

        if (r.getStatus() == ReservationStatus.CANCELLED) {
            return;
        }

        r.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(r);
    }

    @Transactional
    public void cancelAllActiveByFlightForUser(String userEmail, Long flightId) {
        if (userEmail == null || userEmail.isBlank() || flightId == null) {
            throw new IllegalArgumentException(
                    ReservationServiceConstants.ERR_USER_EMAIL_AND_FLIGHT_ID_REQUIRED
            );
        }
        List<Reservation> active = reservationRepository
                .findByUser_EmailAndFlight_IdAndStatus(
                        userEmail, flightId, ReservationStatus.ACTIVE
                );
        if (active.isEmpty()) {
            return;
        }
        for (Reservation r : active) {
            r.setStatus(ReservationStatus.CANCELLED);
        }
        reservationRepository.saveAll(active);
    }

    @Transactional
    public void cancelSeatIfActive(String userEmail, Long flightId, String seatNumber) {
        if (userEmail == null || userEmail.isBlank()
                || flightId == null || seatNumber == null || seatNumber.isBlank()) {
            throw new IllegalArgumentException(
                    ReservationServiceConstants.ERR_USER_EMAIL_FLIGHT_ID_SEAT_REQUIRED
            );
        }
        Reservation r = reservationRepository
                .findFirstByUser_EmailAndFlight_IdAndSeatNumberAndStatus(
                        userEmail, flightId, seatNumber, ReservationStatus.ACTIVE
                )
                .orElseThrow(() -> new NotFoundException(
                        ReservationServiceConstants.ERR_ACTIVE_RESERVATION_FOR_SEAT_NOT_FOUND
                ));
        r.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(r);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> listMine(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException(ReservationServiceConstants.ERR_USER_EMAIL_REQUIRED);
        }
        return reservationRepository.findByUser_EmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getOccupiedSeats(Long flightId) {
        if (flightId == null) {
            throw new IllegalArgumentException("Flight ID is required");
        }
        List<Reservation> activeReservations = reservationRepository
                .findByFlight_IdAndStatusOrderBySeatNumberAsc(flightId, ReservationStatus.ACTIVE);
        
        return activeReservations.stream()
                .map(Reservation::getSeatNumber)
                .filter(seat -> seat != null && !seat.isBlank())
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsPaid(String userEmail, Long flightId) {
        if (userEmail == null || userEmail.isBlank() || flightId == null) {
            throw new IllegalArgumentException("User email and flight ID are required");
        }
        
        List<Reservation> reservations = reservationRepository
                .findByUser_EmailAndFlight_IdAndStatus(userEmail, flightId, ReservationStatus.ACTIVE);
        
        if (reservations.isEmpty()) {
            throw new NotFoundException("No active reservation found for this flight");
        }
        
        for (Reservation r : reservations) {
            r.setPaid(true);
            r.setPaidAt(Instant.now());
        }
        
        reservationRepository.saveAll(reservations);
    }

    private ReservationResponseDTO toDto(Reservation r) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(r.getId());
        dto.setSeatNumber(r.getSeatNumber());
        dto.setStatus(r.getStatus());
        dto.setPaid(r.getPaid());
        dto.setPaidAt(r.getPaidAt());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setFlightId(r.getFlight().getId());
        dto.setAirline(r.getFlight().getAirline());
        dto.setOrigin(r.getFlight().getOrigin());
        dto.setDestination(r.getFlight().getDestination());
        dto.setDepartureAt(r.getFlight().getDepartureAt());
        dto.setArriveAt(r.getFlight().getArriveAt());
        dto.setPrice(r.getFlight().getPrice());
        return dto;
    }
}