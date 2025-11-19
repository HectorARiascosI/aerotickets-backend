package com.aerotickets.repository;

import com.aerotickets.entity.RouteProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteProfileRepository extends JpaRepository<RouteProfile, Long> {

    Optional<RouteProfile> findByOriginIataAndDestinationIata(String originIata, String destinationIata);
}