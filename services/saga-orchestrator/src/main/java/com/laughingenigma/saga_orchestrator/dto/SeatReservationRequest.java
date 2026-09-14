package com.laughingenigma.saga_orchestrator.dto;

import com.laughingenigma.saga_orchestrator.saga.registration_saga.dto.RegistrationContextDto;

public record SeatReservationRequest (
        String registrationId,
        Long eventId,
//---------------------------------
        Long customerId,
        String username,
        String email,
        String firstName,
        String lastName
) implements RegistrationContextDto {
}
