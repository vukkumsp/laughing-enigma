package com.laughingenigma.event_service.dto;

import com.laughingenigma.event_service.entity.Currency;

import java.math.BigDecimal;

public record SeatUnreserveResponse(
        String registrationId,
        Long eventId,
        boolean success
) {
}
