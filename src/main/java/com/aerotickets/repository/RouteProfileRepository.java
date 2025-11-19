package com.aerotickets.repository;

import com.aerotickets.entity.RouteProfileCo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteProfileRepository extends JpaRepository<RouteProfileCo, Long> {

    Optional<RouteProfileCo> findByOriginIataAndDestinationIata(String originIata, String destinationIata);
}