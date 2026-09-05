package com.laughingenigma.saga_orchestrator.dto;

public record SeatUnreserveResponse(
        String registrationId,
        Long eventId,
        boolean success
) {
}
