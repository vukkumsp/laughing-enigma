package com.laughingenigma.aggregator_service.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentSummary(
    Long id,
    String registrationId,
    Long customerId,
    BigDecimal amount,
    String currency,
    String razorpayOrderId,
    String razorpayPaymentId,
    Instant createdAt,
    Instant updatedAt,
    Instant paidAt,
    PaymentStatus status
) {
}
