package com.laughingenigma.payment_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        String registrationId,
        Long eventId,

        String eventName,

        @NotNull
        Long customerId,

        @NotNull
        @DecimalMin(value = "1.0")
        BigDecimal amount,

        String currency
) {
}
