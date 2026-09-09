package com.laughingenigma.notification_service.kafka.event;

public record RegistrationFailedPayload(
        String registrationId,
        String userId,
        String email,
        EventDetails event,
        String reason
) implements RegistrationEventPayload {
}
