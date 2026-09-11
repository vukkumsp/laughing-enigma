package com.laughingenigma.saga_orchestrator.dto;

public record CustomerValidationRequest(
        String registrationId,
        Long eventId,
        String username
) {
}
