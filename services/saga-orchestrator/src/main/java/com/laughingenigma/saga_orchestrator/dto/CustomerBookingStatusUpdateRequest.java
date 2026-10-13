package com.laughingenigma.saga_orchestrator.dto;

import java.math.BigDecimal;

public record CustomerBookingStatusUpdateRequest(
        String registrationId,
        Long eventId,
        String username,
        String orderId,
        String currency,
        BigDecimal amount,
        String bookingStatus
) {
}
