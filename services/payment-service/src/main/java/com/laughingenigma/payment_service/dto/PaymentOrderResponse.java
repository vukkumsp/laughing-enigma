package com.laughingenigma.payment_service.dto;

import java.math.BigDecimal;

public record PaymentOrderResponse(
        String registrationId,
        Long eventId,
        Long customerId,
        String orderId,
        BigDecimal amount,
        String currency,
        String status
) {
}
