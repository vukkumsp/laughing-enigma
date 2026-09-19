package com.laughingenigma.payment_service.controller;

import com.laughingenigma.payment_service.dto.*;
import com.laughingenigma.payment_service.entity.Payment;
import com.laughingenigma.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @Valid @RequestBody PaymentOrderRequest request) {

        return ResponseEntity.ok(
                paymentService.createOrder(request)
        );
    }

    @GetMapping("/ordersPending")
    public ResponseEntity<List<Payment>> getAllOrdersPending(Long customerId){
        List<Payment> orders = paymentService.getOrders(customerId, "CREATED");
        return orders.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PaymentOrderResponse> getOrder(
            @PathVariable String orderId) {

        return ResponseEntity.ok(
                paymentService.getOrder(orderId)
        );
    }
}
