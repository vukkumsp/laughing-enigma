package com.laughingenigma.customer_service.controller;

import com.laughingenigma.customer_service.dto.MeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/customers")
public class Me {

    @GetMapping("/me")
    public ResponseEntity<String> getCustomer(
            @RequestHeader("X-Authenticated-User") String username) {

        return ResponseEntity.ok(
                "Customer service reached. User: " + username
        );
    }
}
