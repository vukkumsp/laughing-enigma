package com.laughingenigma.aggregator_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record EventSummary(
         long id,
         String name,
         String description,
         LocalDateTime eventDate,
         int availableSeats,
         String currency,
         BigDecimal price,
         Instant createdAt,
         Instant updatedAt
) {
}
