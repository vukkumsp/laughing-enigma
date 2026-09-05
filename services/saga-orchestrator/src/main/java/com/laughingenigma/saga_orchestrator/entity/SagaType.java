package com.laughingenigma.saga_orchestrator.entity;

import lombok.Getter;

@Getter
public enum SagaType {
    REGISTRATION("registrationId");
    private final String correlationField;

    SagaType(String correlationField) {
        this.correlationField = correlationField;
    }

}
