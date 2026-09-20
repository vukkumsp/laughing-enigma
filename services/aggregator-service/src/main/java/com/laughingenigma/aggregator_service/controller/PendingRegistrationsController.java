package com.laughingenigma.aggregator_service.controller;

import com.laughingenigma.aggregator_service.dto.PendingRegistrationsResponse;
import com.laughingenigma.aggregator_service.service.PendingRegistrationsAggregator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/pending-registrations")
public class PendingRegistrationsController {

    private final PendingRegistrationsAggregator  pendingRegistrationsAggregator;

    public PendingRegistrationsController(PendingRegistrationsAggregator pendingRegistrationsAggregator) {
        this.pendingRegistrationsAggregator = pendingRegistrationsAggregator;
    }

    @GetMapping
    ResponseEntity<PendingRegistrationsResponse> getPendingRegistrations(){
        PendingRegistrationsResponse response = pendingRegistrationsAggregator.getPendingRegistrations();
        return ResponseEntity
                .ok(response);
    }
}
