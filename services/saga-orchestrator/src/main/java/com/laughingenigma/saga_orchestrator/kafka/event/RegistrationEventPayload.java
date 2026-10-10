package com.laughingenigma.saga_orchestrator.kafka.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.laughingenigma.saga_orchestrator.config.KafkaConfig;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "payloadType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = RegistrationCompletedPayload.class,
                name = KafkaConfig.REGISTRATION_COMPLETED
        ),
        @JsonSubTypes.Type(
                value = RegistrationFailedPayload.class,
                name = KafkaConfig.REGISTRATION_FAILED
        )
})
public sealed interface RegistrationEventPayload
        permits RegistrationCompletedPayload, RegistrationFailedPayload {
    String registrationId();
    String userId();
    String email();
    EventDetails event();
}
