package com.laughingenigma.saga_orchestrator.dto;

public record SeatReservationResponse(
        String registrationId,
        Long eventId,
        boolean success
) {
}
