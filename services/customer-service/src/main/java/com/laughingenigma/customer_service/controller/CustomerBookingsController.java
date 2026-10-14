package com.laughingenigma.customer_service.controller;

import com.laughingenigma.customer_service.dto.Profile;
import com.laughingenigma.customer_service.entity.CustomerBooking;
import com.laughingenigma.customer_service.service.CustomerBookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerBookingsController {

    private final CustomerBookingService  customerBookingService;

    CustomerBookingsController(CustomerBookingService customerBookingService) {
        this.customerBookingService = customerBookingService;
    }

    @GetMapping("/me/bookings")
    public ResponseEntity<List<CustomerBooking>> getAllMyBookings(
            @RequestHeader("X-Authenticated-User") String username) {
        List<CustomerBooking> bookings = customerBookingService.getCustomerBookings(username);
        return ResponseEntity
                .ok(bookings);
    }
}
