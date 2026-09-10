package com.laughingenigma.event_service.dto;

import com.laughingenigma.event_service.entity.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SeatReservationResponse(
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
        Currency currency,
        boolean success
) {
}
