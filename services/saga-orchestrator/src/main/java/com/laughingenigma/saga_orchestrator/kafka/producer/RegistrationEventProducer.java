package com.laughingenigma.saga_orchestrator.kafka.producer;

import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationEvent;

public interface RegistrationEventProducer {

    void publish(RegistrationEvent event);
}
