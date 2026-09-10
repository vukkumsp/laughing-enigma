package com.laughingenigma.saga_orchestrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Value("${spring.application.name")
    public static final String APPLICATION_NAME = "saga_orchestrator";
    public static final String REGISTRATION_EVENTS_TOPIC = "registration-events";
}
