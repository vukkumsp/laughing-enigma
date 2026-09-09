package com.laughingenigma.notification_service.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    public static final String REGISTRATION_EVENTS_TOPIC = "registration-events";
    public static final String NOTIFICATION_SERVICE_GROUP_ID = "notification-service";
}
