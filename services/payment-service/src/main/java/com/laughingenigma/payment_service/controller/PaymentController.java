package com.laughingenigma.payment_service.controller;

import com.laughingenigma.payment_service.dto.PaymentOrderResponse;
import com.laughingenigma.payment_service.dto.PaymentRequest;
import com.laughingenigma.payment_service.dto.PaymentVerificationRequest;
import com.laughingenigma.payment_service.dto.PaymentVerificationResponse;
import com.laughingenigma.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @Valid @RequestBody PaymentRequest request) {

        return ResponseEntity.ok(
                paymentService.createOrder(request)
        );
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PaymentOrderResponse> getOrder(
            @PathVariable String orderId) {

        return ResponseEntity.ok(
                paymentService.getOrder(orderId)
        );
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
