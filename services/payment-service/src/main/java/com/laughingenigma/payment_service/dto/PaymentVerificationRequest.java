package com.laughingenigma.payment_service.dto;

public record PaymentVerificationRequest(
        String razorpayOrderId,
        String razorpayPaymentId,
        String razorpaySignature
) {
}