package com.laughingenigma.notification_service.kafka.event;

public sealed interface RegistrationEventPayload
        permits RegistrationCompletedPayload, RegistrationFailedPayload {
    String registrationId();
    String userId();
    String email();
    EventDetails event();
}
