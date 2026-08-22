package com.laughingenigma.customer_service.controller;

import com.laughingenigma.customer_service.dto.CustomerValidationRequest;
import com.laughingenigma.customer_service.dto.CustomerValidationResponse;
import com.laughingenigma.customer_service.dto.Profile;
import com.laughingenigma.customer_service.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerValidationController {

    private final CustomerService customerService;

    public CustomerValidationController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/validate")
    public ResponseEntity<CustomerValidationResponse> getCustomer(
            @RequestBody CustomerValidationRequest request,
            @RequestHeader("X-Authenticated-User") String username) {

        boolean valid = customerService.validateCustomer(username);
        CustomerValidationResponse customerValidationResponse
                = new CustomerValidationResponse(request.registrationId(), request.eventId(), valid, request.username());

        return ResponseEntity
                .ok(customerValidationResponse);
    }
}
