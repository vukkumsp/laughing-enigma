package com.laughingenigma.saga_orchestrator.dto;

import com.laughingenigma.saga_orchestrator.saga.registration_saga.dto.RegistrationContextDto;

public record RegistrationRequest (
        String registrationId,
        Long eventId
) implements RegistrationContextDto {
}
