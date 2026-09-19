package com.laughingenigma.saga_orchestrator.dto;

import com.laughingenigma.saga_orchestrator.saga.registration_saga.dto.RegistrationContextDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public record PaymentOrderResponse(
        String registrationId,
        Long eventId,
//-----------------------------------
        Long customerId,
        String username,
        String email,
        String firstName,
        String lastName,
//-----------------------------------
        String eventName,
        LocalDateTime eventDate,
        BigDecimal price,
        String currency,
        String orderId,
        String status
) implements RegistrationContextDto {
}
