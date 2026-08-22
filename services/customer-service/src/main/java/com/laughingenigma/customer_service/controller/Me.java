package com.laughingenigma.customer_service.controller;

import com.laughingenigma.customer_service.dto.MeResponse;
import com.laughingenigma.customer_service.dto.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/customers-deprecated")
public class Me {

    @GetMapping("/me")
    public ResponseEntity<Profile> getCustomer(
            @RequestHeader("X-Authenticated-User") String username) {

        Profile profile = new Profile(username, null, null);

        return ResponseEntity
                .ok(profile);
    }
}
