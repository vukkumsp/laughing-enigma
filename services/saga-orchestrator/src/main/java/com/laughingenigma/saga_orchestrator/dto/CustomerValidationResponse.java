package com.laughingenigma.saga_orchestrator.dto;

import com.laughingenigma.saga_orchestrator.saga.registration_saga.dto.RegistrationContextDto;

public record CustomerValidationResponse(
        String registrationId,
        Long eventId,
//---------------------------------
        boolean valid,
        Long customerId,
        String username,
        String email,
        String firstName,
        String lastName
) implements RegistrationContextDto {
}
