package com.laughingenigma.saga_orchestrator.dto;

public record CustomerValidationRequest(
        String registrationId,
        String username,
        Long eventId
) {
}
