package com.laughingenigma.customer_service.dto;

import com.laughingenigma.customer_service.entity.Currency;

import java.math.BigDecimal;

public record CustomerBookingStatusUpdateRequest(
        String registrationId,
        Long eventId,
        String username,
        String orderId,
        Currency currency,
        BigDecimal amount,
        String bookingStatus
) {
}
