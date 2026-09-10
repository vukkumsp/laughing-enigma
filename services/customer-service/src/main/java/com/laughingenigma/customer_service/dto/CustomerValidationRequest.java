package com.laughingenigma.customer_service.dto;

public record CustomerValidationRequest(
        String registrationId,
        Long eventId,

        String username
) {
}
