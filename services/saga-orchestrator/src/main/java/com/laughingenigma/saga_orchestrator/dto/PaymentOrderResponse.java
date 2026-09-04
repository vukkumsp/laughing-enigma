package com.laughingenigma.saga_orchestrator.dto;

import java.math.BigDecimal;

public record PaymentOrderResponse(
        String registrationId,
        Long eventId,

        String orderId,
        BigDecimal amount,
        String currency
) {
}
