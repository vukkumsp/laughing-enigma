package com.laughingenigma.aggregator_service.dto;

import java.time.LocalDateTime;

public record EventSummary(
        String eventId,
        String name,
        LocalDateTime eventDate
) {
}
