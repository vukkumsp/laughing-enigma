package com.laughingenigma.saga_orchestrator.kafka.event;

import java.util.UUID;

public record RegistrationFailedPayload(
        String registrationId,
        String userId,
        String email,
        EventDetails event,
        String reason
) implements RegistrationEventPayload {
}
