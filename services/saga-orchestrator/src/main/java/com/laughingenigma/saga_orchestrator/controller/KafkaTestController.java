package com.laughingenigma.saga_orchestrator.controller;

import com.laughingenigma.saga_orchestrator.kafka.event.EventDetails;
import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationCompletedPayload;
import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationEvent;
import com.laughingenigma.saga_orchestrator.kafka.producer.RegistrationEventProducer;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test/kafka")
@Profile("dev")
public class KafkaTestController {

    private final RegistrationEventProducer producer;

    public KafkaTestController(RegistrationEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/registration-status/{state}")
    public void publishCompleted(@PathVariable int state) {

        EventDetails eventDetails = new EventDetails(
                1L,
                "Java Conference 2026",
                Instant.parse("2026-10-10T10:00:00Z"),
                Instant.parse("2026-10-10T17:00:00Z")
        );

        RegistrationCompletedPayload payload =
                new RegistrationCompletedPayload(
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        "test@example.com",
                        eventDetails
                );

        RegistrationEvent event = new RegistrationEvent(
                UUID.randomUUID(),
                state == 1 ? "RegistrationCompleted":"RegistrationFailed",
                1,
                Instant.now(),
                "saga-orchestrator",
                payload
        );

        producer.publish(event);
    }
}
