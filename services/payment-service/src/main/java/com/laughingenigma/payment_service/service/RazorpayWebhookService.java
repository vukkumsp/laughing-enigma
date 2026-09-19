package com.laughingenigma.payment_service.service;

import com.laughingenigma.payment_service.dto.PaymentVerifyResponse;
import com.laughingenigma.payment_service.entity.Payment;
import com.laughingenigma.payment_service.entity.PaymentStatus;
import com.laughingenigma.payment_service.publisher.PaymentVerifyResponsePublisher;
import com.laughingenigma.payment_service.repository.PaymentRepository;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Service
public class RazorpayWebhookService {

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    private final PaymentRepository paymentRepository;
    private final PaymentVerifyResponsePublisher publisher;

    public RazorpayWebhookService(
            PaymentRepository paymentRepository,
            PaymentVerifyResponsePublisher publisher) {
        this.paymentRepository = paymentRepository;
        this.publisher = publisher;
    }

    public boolean validateSignature(
            String rawBody,
            String signature
    ) {
        try {
            return Utils.verifyWebhookSignature(
                    rawBody,
                    signature,
                    webhookSecret
            );
        } catch (RazorpayException e) {
            throw new RuntimeException(
                    "Failed to validate Razorpay webhook signature",
                    e
            );
        }
    }

    public void updateSaga(String rawBody) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root = objectMapper.readTree(rawBody);

        String event = root.get("event").asText();

        if(!(event.equalsIgnoreCase("payment.captured") ||
                (event.equalsIgnoreCase("payment.failed")))) {
            // only allow payment.captured or payment.failed events
            // to publish as verified.
            // others are ignored for now
            return;
        }

        JsonNode paymentEntity =
                root.get("payload")
                        .get("payment")
                        .get("entity");

        String paymentId =
                paymentEntity.get("id").asText();

        String orderId =
                paymentEntity.get("order_id").asText();

        String status =
                paymentEntity.get("status").asText();

        Payment payment = this.paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow();

        payment.setRazorpayPaymentId(paymentId);
        payment.setPaidAt(Instant.now());

        System.out.println("status obtained from rawBody is " + status);

        switch (status){
            case "captured":
                payment.setStatus(PaymentStatus.SUCCESS);
                break;
            case "failed":
                payment.setStatus(PaymentStatus.FAILED);
                break;
        }

        //save db status
        this.paymentRepository.save(payment);

        PaymentVerifyResponse paymentVerifyResponse = new PaymentVerifyResponse(
                payment.getRegistrationId(),
                payment.getCustomerId(),
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                payment.getStatus().name()
        );

        //resuming saga flow
        publisher.publish(paymentVerifyResponse);
    }
}
