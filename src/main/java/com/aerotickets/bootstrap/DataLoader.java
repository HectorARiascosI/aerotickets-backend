package com.aerotickets.bootstrap;

import com.aerotickets.entity.Flight;
import com.aerotickets.repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initFlights(FlightRepository flightRepository) {
        return args -> {
            if (flightRepository.count() == 0) {
                flightRepository.save(Flight.builder()
                        .airline("Aerolíneas Demo")
                        .origin("BOG")
                        .destination("MDE")
                        .departureAt(LocalDateTime.now().plusDays(3).withHour(8).withMinute(0))
                        .arriveAt(LocalDateTime.now().plusDays(3).withHour(9).withMinute(15))
                        .totalSeats(150)
                        .price(new BigDecimal("220000"))
                        .build());

                flightRepository.save(Flight.builder()
                        .airline("Demo Air")
                        .origin("CLO")
                        .destination("CTG")
                        .departureAt(LocalDateTime.now().plusDays(7).withHour(10).withMinute(30))
                        .arriveAt(LocalDateTime.now().plusDays(7).withHour(12).withMinute(0))
                        .totalSeats(120)
                        .price(new BigDecimal("380000"))
                        .build());
            }
        };
    }
}