package com.laughingenigma.event_service.dto;

public record SeatReservationRequest (
        String registrationId,
        String username,
        Long eventId
) {
}
