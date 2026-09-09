package com.laughingenigma.notification_service.kafka.consumer;

import com.laughingenigma.notification_service.config.KafkaConfig;
import com.laughingenigma.notification_service.kafka.dispatcher.RegistrationEventDispatcher;
import com.laughingenigma.notification_service.kafka.event.RegistrationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationEventConsumer {
    private final RegistrationEventDispatcher dispatcher;

    public RegistrationEventConsumer(RegistrationEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @KafkaListener(
            topics = KafkaConfig.REGISTRATION_EVENTS_TOPIC,
            groupId = KafkaConfig.NOTIFICATION_SERVICE_GROUP_ID
    )
    public void consume(RegistrationEvent event) {
        System.out.println("Received kafka event type: " + event.eventType());

        dispatcher.dispatch(event);
    }
}
