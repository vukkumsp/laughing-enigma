package com.laughingenigma.event_service.dto;

import com.laughingenigma.event_service.entity.Currency;

import java.math.BigDecimal;

public record SeatReservationResponse(
        String registrationId,
        Long eventId,
        Long customerId,
        BigDecimal price,
        Currency currency,
        boolean success
) {
}
