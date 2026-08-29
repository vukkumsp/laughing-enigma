package com.laughingenigma.payment_service.dto;

public record PaymentFailedEvent(
        String sagaId,
        Long customerId,
        String razorpayOrderId,
        String reason
) {
}