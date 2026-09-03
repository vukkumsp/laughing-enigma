package com.laughingenigma.payment_service.dto;

public record PaymentVerifyResponse (
        String registrationId,
        Long eventId,

        String orderId,
        String paymentId,
        String status
){
}
