package com.laughingenigma.event_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record EventDetailsResponse(
        Long eventId,
        String name,
        String description,
        String eventDate,
        String category,
        String bannerImageUrl,
        String duration,
        int availableSeats,
        BigDecimal ticketPrice,
        String currency,
        VenueResponse venue,
        HostResponse host,
        List<String> highlights,
        List<AgendaItemResponse> agenda
) {
}
