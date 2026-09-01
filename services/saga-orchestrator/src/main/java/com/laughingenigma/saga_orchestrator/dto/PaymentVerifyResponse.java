package com.laughingenigma.saga_orchestrator.dto;

public record PaymentVerifyResponse (
        String registrationId,
        Long eventId,
        String status
){
}
