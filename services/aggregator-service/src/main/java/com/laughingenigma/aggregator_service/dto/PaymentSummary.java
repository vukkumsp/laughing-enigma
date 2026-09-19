package com.laughingenigma.aggregator_service.dto;

import java.math.BigDecimal;

public record PaymentSummary(
        String paymentId,
        String razorpayOrderId,
        BigDecimal price,
        String currency,
        String status
) {
}
