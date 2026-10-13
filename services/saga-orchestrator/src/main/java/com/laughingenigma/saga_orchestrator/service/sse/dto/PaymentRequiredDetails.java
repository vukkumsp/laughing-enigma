package com.laughingenigma.saga_orchestrator.service.sse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequiredDetails(
        String registrationId,
        Long eventId,
//-----------------------------------
        Long customerId,
        String username,
        String email,
        String firstName,
        String lastName,
//-----------------------------------
        String eventName,
        LocalDateTime eventDate,
        BigDecimal price,
        String currency,
        String orderId,
        String status
) {
}
