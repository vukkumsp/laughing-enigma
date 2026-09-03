package com.laughingenigma.saga_orchestrator.dto;

public record RegistrationRequest(
        String registrationId,
        Long eventId
) {
}
