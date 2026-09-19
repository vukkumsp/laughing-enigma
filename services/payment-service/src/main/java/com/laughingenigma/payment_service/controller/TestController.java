package com.laughingenigma.payment_service.controller;

import com.laughingenigma.payment_service.dto.PaymentVerificationRequest;
import com.laughingenigma.payment_service.dto.PaymentVerificationResponse;
import com.laughingenigma.payment_service.service.PaymentService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Profile("dev")
public class TestController {

    private final PaymentService paymentService;

    public TestController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("test")
    String test() {
        return "Test Successful";
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @RequestBody PaymentVerificationRequest request
    ) {
        return ResponseEntity.ok(
                paymentService.verifyPayment(request)
        );
    }
}
