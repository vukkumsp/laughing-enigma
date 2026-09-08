package com.laughingenigma.payment_service.dto.event;

public record PaymentFailureResponse (
        String registrationId,
        Long eventId
) {
}
