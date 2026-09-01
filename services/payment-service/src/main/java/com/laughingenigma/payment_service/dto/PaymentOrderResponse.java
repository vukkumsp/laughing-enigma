package com.laughingenigma.payment_service.dto;

import java.math.BigDecimal;

public record PaymentOrderResponse(
        String registrationId,
        Long eventId,

        BigDecimal amount,
        String currency,
        String status
) {
}
