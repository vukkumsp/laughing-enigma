package com.laughingenigma.saga_orchestrator.dto;

import java.math.BigDecimal;

public record PaymentOrderRequest(
        String registrationId,
        Long eventId,

        BigDecimal amount,
        String currency
) {
}
