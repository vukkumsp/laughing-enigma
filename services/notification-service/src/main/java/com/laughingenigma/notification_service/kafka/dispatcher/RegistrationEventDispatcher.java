package com.laughingenigma.notification_service.kafka.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laughingenigma.notification_service.kafka.event.RegistrationCompletedPayload;
import com.laughingenigma.notification_service.kafka.event.RegistrationEvent;
import com.laughingenigma.notification_service.kafka.event.RegistrationFailedPayload;
import org.springframework.stereotype.Component;

@Component
public class RegistrationEventDispatcher {

    private final ObjectMapper objectMapper;

    public RegistrationEventDispatcher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void dispatch(RegistrationEvent event) {

        switch (event.eventType()) {

            case "RegistrationCompleted" -> {
                RegistrationCompletedPayload payload =
                        objectMapper.convertValue(
                                event.payload(),
                                RegistrationCompletedPayload.class
                        );

                handleCompleted(event, payload);
            }

            case "RegistrationFailed" -> {
                RegistrationFailedPayload payload =
                        objectMapper.convertValue(
                                event.payload(),
                                RegistrationFailedPayload.class
                        );

                handleFailed(event, payload);
            }

            default -> throw new IllegalArgumentException(
                    "Unknown registration event type: " + event.eventType()
            );
        }
    }

    private void handleCompleted(
            RegistrationEvent event,
            RegistrationCompletedPayload payload) {

        System.out.println(
                "Registration completed: " +
                        payload.registrationId()
        );
    }

    private void handleFailed(
            RegistrationEvent event,
            RegistrationFailedPayload payload) {

        System.out.println(
                "Registration failed: " +
                        payload.registrationId()
        );
    }
}