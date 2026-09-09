package com.laughingenigma.saga_orchestrator.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    public static final String REGISTRATION_EVENTS_TOPIC = "registration-events";
}
