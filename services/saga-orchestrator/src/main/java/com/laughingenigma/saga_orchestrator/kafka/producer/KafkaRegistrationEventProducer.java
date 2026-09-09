package com.laughingenigma.saga_orchestrator.kafka.producer;

import com.laughingenigma.saga_orchestrator.config.KafkaConfig;
import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaRegistrationEventProducer
        implements RegistrationEventProducer {

    private final KafkaTemplate<String, RegistrationEvent> kafkaTemplate;

    public KafkaRegistrationEventProducer(
            KafkaTemplate<String, RegistrationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(RegistrationEvent event) {
        String registrationId =
                event.payload().registrationId();

        kafkaTemplate.send(
                KafkaConfig.REGISTRATION_EVENTS_TOPIC,
                registrationId,
                event
        );
    }
}
