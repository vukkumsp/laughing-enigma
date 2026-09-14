package com.laughingenigma.saga_orchestrator.dto;

import com.laughingenigma.saga_orchestrator.saga.registration_saga.dto.RegistrationContextDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

public record SeatReservationResponse(
        String registrationId,
        Long eventId,
//---------------------------------
        Long customerId,
        String username,
        String email,
        String firstName,
        String lastName,
//---------------------------------
        String eventName,
        LocalDateTime eventDate,
        BigDecimal price,
        String currency,
        boolean success
) implements RegistrationContextDto {
}
