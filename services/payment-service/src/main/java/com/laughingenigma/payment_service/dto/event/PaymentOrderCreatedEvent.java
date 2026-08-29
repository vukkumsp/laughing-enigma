package com.laughingenigma.payment_service.dto.event;

import java.math.BigDecimal;

public record PaymentOrderCreatedEvent(
        String sagaId,
        Long customerId,
        String razorpayOrderId,
        BigDecimal amount,
        String currency
) {
}
