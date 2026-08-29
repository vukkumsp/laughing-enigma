package com.laughingenigma.payment_service.dto.event;

import java.math.BigDecimal;

public record PaymentRequestedEvent(
        String sagaId,
        Long customerId,
        BigDecimal amount,
        String currency
) {
}
