package com.laughingenigma.saga_orchestrator.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentVerifyRequest (
        String registrationId,
        Long eventId,
//---------------------------------------
        Long customerId,
        String username,
        String email,
        String firstName,
        String lastName,
//---------------------------------------
        String eventName,
        LocalDateTime eventDate,
        String razorpayOrderId,
        String razorpayPaymentId,
        String razorpaySignature
) {
}
