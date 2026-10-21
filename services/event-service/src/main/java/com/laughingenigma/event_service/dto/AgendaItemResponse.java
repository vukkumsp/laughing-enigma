package com.laughingenigma.event_service.dto;

public record AgendaItemResponse(
        String time,
        String title,
        String description
) {
}
