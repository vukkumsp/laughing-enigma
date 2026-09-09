package com.laughingenigma.saga_orchestrator.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record RegistrationEvent(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        String source,
        RegistrationEventPayload payload
) {
}
