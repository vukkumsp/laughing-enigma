package com.laughingenigma.event_service.dto;

public record HostResponse(
        String name,
        String title,
        String imageUrl,
        String bio
) {
}
