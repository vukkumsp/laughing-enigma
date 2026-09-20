package com.laughingenigma.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

    // to get forwards for all Http methods and not just GET or POST
    @RequestMapping("/{service}")
    public ResponseEntity<?> fallback(
            @PathVariable String service) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "code", "SERVICE_UNAVAILABLE",
                        "service", service,
                        "message", service + " service is temporarily unavailable"
                ));
    }
}
