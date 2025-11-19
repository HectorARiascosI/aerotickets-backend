package com.aerotickets.controller;

import com.aerotickets.constants.HealthConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping(HealthConstants.HEALTH_PATH)
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                HealthConstants.FIELD_STATUS, HealthConstants.STATUS_UP,
                HealthConstants.FIELD_SERVICE, HealthConstants.SERVICE_NAME
        ));
    }
}