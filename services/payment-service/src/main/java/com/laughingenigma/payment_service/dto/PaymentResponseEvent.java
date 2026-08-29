package com.laughingenigma.payment_service.dto;

import com.laughingenigma.payment_service.entity.PaymentStatus;

public record PaymentResponseEvent(
        String sagaId,
        Long customerId,
        String razorpayOrderId,
        String razorpayPaymentId,
        PaymentStatus status
) {
}
