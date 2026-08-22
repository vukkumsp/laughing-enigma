package com.laughingenigma.event_service.dto;

public record SeatReservationResponse(
        String registrationId,
        Long eventId,
        boolean success
) {
}
