package com.laughingenigma.saga_orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PaymentVerifyRequest (
        String registrationId,
        Long eventId,

        String razorpayOrderId,
        String razorpayPaymentId,
        String razorpaySignature
) {
}
