package com.laughingenigma.saga_orchestrator.dto;

public record PaymentFailureResponse (
    String registrationId,
    Long eventId
) {
}
