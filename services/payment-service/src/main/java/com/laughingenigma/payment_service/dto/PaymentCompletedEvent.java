package com.laughingenigma.payment_service.dto;

public record PaymentCompletedEvent(
        String sagaId,
        Long customerId,
        String razorpayOrderId,
        String razorpayPaymentId
) {
}
