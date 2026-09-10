package com.laughingenigma.saga_orchestrator.dto;

public record SeatReservationRequest (
        String registrationId,
        Long eventId,
//---------------------------------
        Long customerId,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
