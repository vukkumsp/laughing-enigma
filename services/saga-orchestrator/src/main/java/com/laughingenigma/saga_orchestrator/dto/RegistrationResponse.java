package com.laughingenigma.saga_orchestrator.dto;

public record RegistrationResponse(
        String registrationId,
        Long eventId,
        String status
) {
}
