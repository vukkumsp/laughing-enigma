package com.laughingenigma.aggregator_service.controller;

import com.laughingenigma.aggregator_service.dto.RegistrationsRequest;
import com.laughingenigma.aggregator_service.dto.RegistrationsResponse;
import com.laughingenigma.aggregator_service.dto.RegistrationsSummary;
import com.laughingenigma.aggregator_service.service.RegistrationsAggregator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me/registrations")
public class RegistrationsController {

    private final RegistrationsAggregator registrationsAggregator;

    public RegistrationsController(RegistrationsAggregator registrationsAggregator) {
        this.registrationsAggregator = registrationsAggregator;
    }

    @PostMapping
    ResponseEntity<RegistrationsResponse> getRegistrations(@RequestHeader("X-Authenticated-User") String username){
        RegistrationsRequest request =  new RegistrationsRequest(username);
        List<RegistrationsSummary> registrationsSummaryList = registrationsAggregator.getRegistrations(request.customerUsername());
        RegistrationsResponse response = new RegistrationsResponse(
                request.customerUsername(),
                registrationsSummaryList
        );
        return ResponseEntity
                .ok(response);
    }
}
