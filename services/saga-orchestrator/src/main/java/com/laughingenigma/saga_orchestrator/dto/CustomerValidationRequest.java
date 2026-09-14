package com.laughingenigma.saga_orchestrator.dto;

import com.laughingenigma.saga_orchestrator.saga.registration_saga.dto.RegistrationContextDto;

public record CustomerValidationRequest(
        String registrationId,
        Long eventId,
        String username
) implements RegistrationContextDto {
}
