package com.laughingenigma.saga_orchestrator.dto;

public record PaymentFailureRequest(
        String registrationId,
        Long eventId
) {
}
