package com.laughingenigma.notification_service.kafka.event;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record RegistrationEvent (
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        String source,
//        RegistrationEventPayload payload
        JsonNode payload
) {
}
