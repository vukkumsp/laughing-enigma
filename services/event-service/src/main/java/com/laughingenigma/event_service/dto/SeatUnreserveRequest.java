package com.laughingenigma.event_service.dto;

public record SeatUnreserveRequest(
        String registrationId,
        Long eventId
) {
}
