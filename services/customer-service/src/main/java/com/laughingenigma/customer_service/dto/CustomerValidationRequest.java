package com.laughingenigma.customer_service.dto;

public record CustomerValidationRequest(
        String registrationId,
        String username,
        Long eventId
) {
}
