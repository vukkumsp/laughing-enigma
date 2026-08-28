package com.laughingenigma.payment_service.dto;

public record PaymentResponse(
        String registrationId,
        Long eventId,
        boolean success
) {
}
