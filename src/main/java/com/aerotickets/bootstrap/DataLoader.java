package com.aerotickets.bootstrap;

import com.aerotickets.constants.DataLoaderConstants;
import com.aerotickets.entity.Flight;
import com.aerotickets.repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initFlights(FlightRepository flightRepository) {
        return args -> {
            if (flightRepository.count() == 0) {
                flightRepository.save(
                        Flight.builder()
                                .airline(DataLoaderConstants.FLIGHT_1_AIRLINE)
                                .origin(DataLoaderConstants.FLIGHT_1_ORIGIN)
                                .destination(DataLoaderConstants.FLIGHT_1_DESTINATION)
                                .departureAt(
                                        LocalDateTime.now()
                                                .plusDays(DataLoaderConstants.FLIGHT_1_DAYS_OFFSET)
                                                .withHour(DataLoaderConstants.FLIGHT_1_DEPARTURE_HOUR)
                                                .withMinute(DataLoaderConstants.FLIGHT_1_DEPARTURE_MINUTE)
                                )
                                .arriveAt(
                                        LocalDateTime.now()
                                                .plusDays(DataLoaderConstants.FLIGHT_1_DAYS_OFFSET)
                                                .withHour(DataLoaderConstants.FLIGHT_1_ARRIVAL_HOUR)
                                                .withMinute(DataLoaderConstants.FLIGHT_1_ARRIVAL_MINUTE)
                                )
                                .totalSeats(DataLoaderConstants.FLIGHT_1_TOTAL_SEATS)
                                .price(DataLoaderConstants.FLIGHT_1_PRICE)
                                .build()
                );

                flightRepository.save(
                        Flight.builder()
                                .airline(DataLoaderConstants.FLIGHT_2_AIRLINE)
                                .origin(DataLoaderConstants.FLIGHT_2_ORIGIN)
                                .destination(DataLoaderConstants.FLIGHT_2_DESTINATION)
                                .departureAt(
                                        LocalDateTime.now()
                                                .plusDays(DataLoaderConstants.FLIGHT_2_DAYS_OFFSET)
                                                .withHour(DataLoaderConstants.FLIGHT_2_DEPARTURE_HOUR)
                                                .withMinute(DataLoaderConstants.FLIGHT_2_DEPARTURE_MINUTE)
                                )
                                .arriveAt(
                                        LocalDateTime.now()
                                                .plusDays(DataLoaderConstants.FLIGHT_2_DAYS_OFFSET)
                                                .withHour(DataLoaderConstants.FLIGHT_2_ARRIVAL_HOUR)
                                                .withMinute(DataLoaderConstants.FLIGHT_2_ARRIVAL_MINUTE)
                                )
                                .totalSeats(DataLoaderConstants.FLIGHT_2_TOTAL_SEATS)
                                .price(DataLoaderConstants.FLIGHT_2_PRICE)
                                .build()
                );
            }
        };
    }
}