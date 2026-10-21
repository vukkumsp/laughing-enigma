package com.laughingenigma.event_service.dto;

public record VenueResponse(
        String type,
        String name,
        String address,
        String timezone,
        String platform,
        String accessInstructions,
        VenueResponse inPerson,
        VenueResponse online
) {
}
