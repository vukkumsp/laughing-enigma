package com.laughingenigma.saga_orchestrator.service.sse.dto;

public record PaymentStatusDetails(
        String registrationId,
        Long eventId,
        String status
) {
}
