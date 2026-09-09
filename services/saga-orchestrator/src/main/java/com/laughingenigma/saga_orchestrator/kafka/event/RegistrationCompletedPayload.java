package com.laughingenigma.saga_orchestrator.kafka.event;

import java.util.UUID;

public record RegistrationCompletedPayload(
        String registrationId,
        String userId,
        String email,
        EventDetails event
) implements RegistrationEventPayload {
}
