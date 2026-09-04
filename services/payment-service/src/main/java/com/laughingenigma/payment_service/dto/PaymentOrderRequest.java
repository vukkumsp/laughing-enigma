package com.laughingenigma.payment_service.dto;

import java.math.BigDecimal;

public record PaymentOrderRequest(
        String registrationId,
        Long eventId,
        Long customerId,
        BigDecimal amount,
        String currency
) {
}
