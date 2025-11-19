package com.aerotickets.dto;

import com.aerotickets.constants.FlightConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightDTO {

    private Long id;
    private String airline;
    private String origin;
    private String destination;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FlightConstants.OFFSET_DATETIME_PATTERN)
    private OffsetDateTime departureAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FlightConstants.OFFSET_DATETIME_PATTERN)
    private OffsetDateTime arriveAt;

    private Integer totalSeats;
    private BigDecimal price;
}