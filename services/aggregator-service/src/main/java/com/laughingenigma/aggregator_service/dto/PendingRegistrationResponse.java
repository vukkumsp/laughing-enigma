package com.laughingenigma.aggregator_service.dto;

public record PendingRegistrationResponse(
        String registrationId,
        EventSummary event,
        PaymentSummary payment
) {
}
