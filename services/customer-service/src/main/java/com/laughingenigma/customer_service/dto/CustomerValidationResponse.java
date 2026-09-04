package com.laughingenigma.customer_service.dto;

public record CustomerValidationResponse(
        String registrationId,
        Long eventId,
        boolean valid,
        Long customerId,
        String username
) {
}
