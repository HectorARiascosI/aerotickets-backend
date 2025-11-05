package com.aerotickets.service;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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
            throw new IllegalArgumentException("User email is required");
        }
        if (dto == null || dto.getFlightId() == null) {
            throw new IllegalArgumentException("Flight id is required");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new NotFoundException("Flight not found"));

        long activeCount = reservationRepository.countByFlight_IdAndStatus(flight.getId(), ReservationStatus.ACTIVE);
        if (activeCount >= flight.getTotalSeats()) {
            throw new ConflictException("No seats available for this flight");
        }

        Integer seat = dto.getSeatNumber();
        if (seat != null) {
            if (seat < 1 || seat > flight.getTotalSeats()) {
                throw new IllegalArgumentException("Seat number out of range");
            }
            boolean seatTaken = reservationRepository.existsByFlight_IdAndSeatNumberAndStatus(
                    flight.getId(), seat, ReservationStatus.ACTIVE
            );
            if (seatTaken) {
                throw new ConflictException("Selected seat is already reserved");
            }
        }

        try {
            Reservation r = Reservation.builder()
                    .user(user)
                    .flight(flight)
                    .seatNumber(seat)
                    .status(ReservationStatus.ACTIVE)
                    .build();

            Reservation saved = reservationRepository.save(r);
            return toDto(saved);

        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("You already have an ACTIVE reservation for this flight or the seat is taken");
        }
    }

    @Transactional
    public void cancel(String userEmail, Long reservationId) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("User email is required");
        }
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation id is required");
        }

        Reservation r = reservationRepository.findByIdAndUser_Email(reservationId, userEmail)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        if (r.getStatus() == ReservationStatus.CANCELLED) {
            return;
        }

        r.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(r);
    }

    @Transactional
    public void cancelAllActiveByFlightForUser(String userEmail, Long flightId) {
        if (userEmail == null || userEmail.isBlank() || flightId == null) {
            throw new IllegalArgumentException("User email and flight id are required");
        }
        List<Reservation> active = reservationRepository
                .findByUser_EmailAndFlight_IdAndStatus(userEmail, flightId, ReservationStatus.ACTIVE);
        if (active.isEmpty()) return;
        for (Reservation r : active) {
            r.setStatus(ReservationStatus.CANCELLED);
        }
        reservationRepository.saveAll(active);
    }

    @Transactional
    public void cancelSeatIfActive(String userEmail, Long flightId, Integer seatNumber) {
        if (userEmail == null || userEmail.isBlank() || flightId == null || seatNumber == null) {
            throw new IllegalArgumentException("User email, flight id and seat number are required");
        }
        Reservation r = reservationRepository
                .findFirstByUser_EmailAndFlight_IdAndSeatNumberAndStatus(
                        userEmail, flightId, seatNumber, ReservationStatus.ACTIVE
                )
                .orElseThrow(() -> new NotFoundException("Active reservation for that seat not found"));
        r.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(r);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> listMine(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("User email is required");
        }
        return reservationRepository.findByUser_EmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ReservationResponseDTO toDto(Reservation r) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(r.getId());
        dto.setSeatNumber(r.getSeatNumber());
        dto.setStatus(r.getStatus());
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