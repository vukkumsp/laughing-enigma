package com.laughingenigma.saga_orchestrator.dto;

import java.time.LocalDateTime;

public record PaymentFailureResponse (
        String registrationId,
        Long eventId,
//---------------------------------------
        Long customerId,
        String username,
        String email,
        String firstName,
        String lastName,
//---------------------------------------
        String eventName,
        LocalDateTime eventDate
) {
}
