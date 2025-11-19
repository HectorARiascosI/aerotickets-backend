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

        long activeCount = reservationRepository
                .countByFlight_IdAndStatus(flight.getId(), ReservationStatus.ACTIVE);
        if (activeCount >= flight.getTotalSeats()) {
            throw new ConflictException(ReservationServiceConstants.ERR_NO_SEATS_AVAILABLE);
        }

        Integer seat = dto.getSeatNumber();
        if (seat != null) {
            if (seat < 1 || seat > flight.getTotalSeats()) {
                throw new IllegalArgumentException(ReservationServiceConstants.ERR_SEAT_OUT_OF_RANGE);
            }
            boolean seatTaken = reservationRepository
                    .existsByFlight_IdAndSeatNumberAndStatus(
                            flight.getId(), seat, ReservationStatus.ACTIVE
                    );
            if (seatTaken) {
                throw new ConflictException(ReservationServiceConstants.ERR_SEAT_TAKEN);
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
            throw new ConflictException(
                    ReservationServiceConstants.ERR_ACTIVE_RESERVATION_OR_SEAT_TAKEN
            );
        }
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
        if (active.isEmpty()) return;
        for (Reservation r : active) {
            r.setStatus(ReservationStatus.CANCELLED);
        }
        reservationRepository.saveAll(active);
    }

    @Transactional
    public void cancelSeatIfActive(String userEmail, Long flightId, Integer seatNumber) {
        if (userEmail == null || userEmail.isBlank()
                || flightId == null || seatNumber == null) {
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