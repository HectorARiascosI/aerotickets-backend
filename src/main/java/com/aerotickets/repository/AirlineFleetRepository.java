package com.aerotickets.repository;

import com.aerotickets.entity.AirlineFleet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirlineFleetRepository extends JpaRepository<AirlineFleet, Long> {

    List<AirlineFleet> findAllByOrderByAirlineNameAsc();
}