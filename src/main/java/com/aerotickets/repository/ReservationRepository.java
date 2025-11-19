package com.aerotickets.repository;

import com.aerotickets.entity.Reservation;
import com.aerotickets.entity.ReservationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    long countByFlight_IdAndStatus(Long flightId, ReservationStatus status);

    boolean existsByFlight_IdAndSeatNumberAndStatus(Long flightId, Integer seatNumber, ReservationStatus status);

    @EntityGraph(attributePaths = {"flight"})
    List<Reservation> findByUser_EmailOrderByCreatedAtDesc(String email);

    @EntityGraph(attributePaths = {"flight"})
    Optional<Reservation> findByIdAndUser_Email(Long id, String email);

    List<Reservation> findByUser_EmailAndFlight_IdAndStatus(String email, Long flightId, ReservationStatus status);

    Optional<Reservation> findFirstByUser_EmailAndFlight_IdAndSeatNumberAndStatus(
            String email, Long flightId, Integer seatNumber, ReservationStatus status
    );

    List<Reservation> findByFlight_IdAndStatusOrderBySeatNumberAsc(
            Long flightId, ReservationStatus status
    );
}