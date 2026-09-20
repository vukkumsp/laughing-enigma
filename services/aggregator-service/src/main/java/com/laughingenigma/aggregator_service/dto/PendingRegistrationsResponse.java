package com.laughingenigma.aggregator_service.dto;

public record PendingRegistrationsResponse(
        String registrationId,
        EventSummary event,
        PaymentSummary payment
) {
}
