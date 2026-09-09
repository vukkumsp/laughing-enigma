package com.laughingenigma.notification_service.kafka.event;

public record RegistrationCompletedPayload(
        String registrationId,
        String userId,
        String email,
        EventDetails event
) implements RegistrationEventPayload {
}
