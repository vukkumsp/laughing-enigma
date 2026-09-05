package com.laughingenigma.saga_orchestrator.dto;

public record SeatUnreserveRequest (
        String registrationId,
        Long eventId
){
}
