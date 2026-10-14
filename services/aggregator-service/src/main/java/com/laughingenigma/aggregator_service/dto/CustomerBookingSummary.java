package com.laughingenigma.aggregator_service.dto;

import java.math.BigDecimal;
import java.time.Instant;


public record CustomerBookingSummary (
     long id,
     String customerUsername,
     String registrationId,
     Long eventId,
     String orderId,
     String currency,
     BigDecimal amount,
     String bookingStatus,
     Instant createdAt,
     Instant updatedAt
){}
