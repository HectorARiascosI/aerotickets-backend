package com.aerotickets.controller;

import com.aerotickets.constants.TestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping(TestConstants.HOLA_PATH)
    public String hola() {
        return TestConstants.HOLA_MESSAGE;
    }
}