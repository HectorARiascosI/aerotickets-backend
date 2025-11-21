package com.aerotickets.controller;

import com.aerotickets.constants.ApiPaths;
import com.aerotickets.constants.ErrorMessages;
import com.aerotickets.dto.PaymentRequestDTO;
import com.aerotickets.entity.Flight;
import com.aerotickets.repository.FlightRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping(ApiPaths.Payments.BASE)
public class PaymentController {

    private final FlightRepository flightRepository;
    private final String successUrl;
    private final String cancelUrl;
    private final String currency;

    public PaymentController(FlightRepository flightRepository,
                             @Value("${stripe.success-url}") String successUrl,
                             @Value("${stripe.cancel-url}") String cancelUrl,
                             @Value("${stripe.currency}") String currency) {
        this.flightRepository = flightRepository;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.currency = currency;
    }

    @PostMapping(ApiPaths.Payments.CHECKOUT_SESSION)
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @RequestBody PaymentRequestDTO request
    ) throws StripeException {

        if (request == null || request.getFlightId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.Reservation.FLIGHT_NOT_FOUND));

        BigDecimal price = flight.getPrice();
        if (price == null) {
            price = BigDecimal.ZERO;
        }

        long unitAmount = price
                .multiply(BigDecimal.valueOf(100L))
                .longValueExact();

        String productName = flight.getAirline() + " " +
                flight.getOrigin() + " → " + flight.getDestination();

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productName)
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(currency)
                        .setUnitAmount(unitAmount)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .addLineItem(lineItem)
                        .build();

        Session session = Session.create(params);

        return ResponseEntity.ok(
                Map.of(
                        "id", session.getId(),
                        "url", session.getUrl()
                )
        );
    }
}