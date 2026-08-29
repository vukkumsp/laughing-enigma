package com.laughingenigma.payment_service.dto.event;

public record PaymentFailedEvent(
        String sagaId,
        Long customerId,
        String razorpayOrderId,
        String reason
) {
}