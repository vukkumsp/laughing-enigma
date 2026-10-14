package com.laughingenigma.aggregator_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record RegistrationsSummary(
        String customerUsername,
        String registrationId,
        String orderId,
        String bookingStatus,

        Long eventId,
        String eventName,
        String eventDescription,
        LocalDateTime eventDate,
        int availableSeats,
        String currency,
        BigDecimal amount
) {}
