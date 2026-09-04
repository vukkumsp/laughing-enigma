package com.laughingenigma.saga_orchestrator.dto;

public record CustomerValidationResponse(
        String registrationId,
        Long eventId,
        boolean valid,
        Long customerId,
        String username
) {
}
