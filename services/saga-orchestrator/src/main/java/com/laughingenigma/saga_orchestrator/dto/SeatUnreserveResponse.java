package com.laughingenigma.saga_orchestrator.dto;

import com.laughingenigma.saga_orchestrator.saga.registration_saga.dto.RegistrationContextDto;

public record SeatUnreserveResponse(
        String registrationId,
        Long eventId,
        boolean success
) implements RegistrationContextDto {
}
