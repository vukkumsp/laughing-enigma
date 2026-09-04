package com.laughingenigma.saga_orchestrator.dto;

import java.math.BigDecimal;

public record SeatReservationResponse(
        String registrationId,
        Long eventId,
        Long customerId,
        BigDecimal price,
        String currency,
        boolean success
) {
}
