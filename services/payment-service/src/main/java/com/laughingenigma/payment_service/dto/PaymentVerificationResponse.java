package com.laughingenigma.payment_service.dto;

public record PaymentVerificationResponse(
        String orderId,
        String paymentId,
        String status
) {
}
