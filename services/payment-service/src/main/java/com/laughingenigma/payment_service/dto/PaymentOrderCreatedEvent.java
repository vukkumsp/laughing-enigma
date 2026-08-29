package com.laughingenigma.payment_service.dto;

import java.math.BigDecimal;

public record PaymentOrderCreatedEvent(
        String sagaId,
        Long customerId,
        String razorpayOrderId,
        BigDecimal amount,
        String currency
) {
}
