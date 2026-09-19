package com.laughingenigma.customer_service.controller;

import com.laughingenigma.customer_service.dto.Profile;
import com.laughingenigma.customer_service.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerProfileController {

    private final CustomerService  customerService;

    public CustomerProfileController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping("/me")
    public ResponseEntity<Profile> getMyProfile(
            @RequestHeader("X-Authenticated-User") String username) {

        Profile profile = new Profile(this.customerService.getCustomer(username));

        return ResponseEntity
                .ok(profile);
    }
}
