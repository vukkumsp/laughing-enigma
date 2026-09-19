package com.laughingenigma.payment_service.controller;

import com.laughingenigma.payment_service.service.RazorpayWebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks/razorpay")
public class RazorpayWebhookController {

    private final RazorpayWebhookService razorpayWebhookService;

    RazorpayWebhookController(RazorpayWebhookService razorpayWebhookService) {
        this.razorpayWebhookService = razorpayWebhookService;
    }

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestHeader("x-razorpay-event-id") String eventId,
            @RequestBody String rawBody) {

        System.out.println("===== RAZORPAY WEBHOOK =====");
        System.out.println("Signature header: " + signature);
        System.out.println("Event ID: " + eventId);
        System.out.println("Raw body: " + rawBody);
        System.out.println("============================");

        // validate signature
        boolean valid = this.razorpayWebhookService.validateSignature(
                rawBody,
                signature
        );

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // process event
        this.razorpayWebhookService.updateSaga(rawBody);

        System.out.println("handleWebhook before returning ResponseEntity.ok().build()");
        return ResponseEntity.ok().build();
    }
}
