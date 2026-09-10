package com.laughingenigma.saga_orchestrator.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentOrderRequest(
        String registrationId,
        Long eventId,
//---------------------------------
        Long customerId,
        String username,
        String email,
        String firstName,
        String lastName,
//---------------------------------
        String eventName,
        LocalDateTime eventDate,
        BigDecimal price,
        String currency
) {
}
