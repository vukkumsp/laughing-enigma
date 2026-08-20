package com.laughingenigma.saga_orchestrator.dto;

public record CustomerValidationResponse(
        boolean valid,
        String username
) {
}
