package com.laughingenigma.payment_service.dto.event;

public record PaymentCompletedEvent(
        String sagaId,
        Long customerId,
        String razorpayOrderId,
        String razorpayPaymentId
) {
}
