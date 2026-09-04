package com.laughingenigma.saga_orchestrator.dto;

public record SeatReservationRequest (
        String registrationId,
        String username,
        Long customerId,
        Long eventId
) {
}
