package com.laughingenigma.payment_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentVerifyRequest (
        String registrationId,
        Long eventId,

//        @JsonProperty("razorpay_order_id")
        String razorpayOrderId,
//        @JsonProperty("razorpay_payment_id")
        String razorpayPaymentId,
//        @JsonProperty("razorpay_signature")
        String razorpaySignature
) {
}
