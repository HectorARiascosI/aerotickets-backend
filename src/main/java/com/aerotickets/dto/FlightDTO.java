package com.aerotickets.dto;

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

    // Acepta ISO-8601 con zona horaria (e.g. ...Z)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX")
    private OffsetDateTime departureAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX")
    private OffsetDateTime arriveAt;

    // Pueden venir null; el controller aplica defaults
    private Integer totalSeats;
    private BigDecimal price;
}