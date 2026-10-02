package com.laughingenigma.saga_orchestrator.entity;

public enum SagaStep {
    //REGISTRATION
    REGISTRATION_STARTED,

    CUSTOMER_VALIDATION,
    CUSTOMER_VALIDATED,
    CUSTOMER_VALIDATION_FAILED,

    SEAT_RESERVATION,
    SEAT_RESERVED,
    SEAT_RESERVATION_FAILED,
    SEAT_RELEASING,

    PAYMENT_REQUIRED,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,

    REGISTRATION_COMPLETED,
    REGISTRATION_COMPENSATED;

    public boolean canTransitionTo(SagaStep next) {

        return switch (this) {

            case REGISTRATION_STARTED ->
                    next == CUSTOMER_VALIDATION;

            case CUSTOMER_VALIDATION ->
                    next == CUSTOMER_VALIDATED
                            || next == CUSTOMER_VALIDATION_FAILED;

            case CUSTOMER_VALIDATED ->
                    next == SEAT_RESERVATION;

            case CUSTOMER_VALIDATION_FAILED ->
                    next == REGISTRATION_COMPENSATED;

            case SEAT_RESERVATION ->
                    next == SEAT_RESERVED
                            || next == SEAT_RESERVATION_FAILED;

            case SEAT_RESERVED ->
                    next == PAYMENT_REQUIRED;

            case SEAT_RESERVATION_FAILED ->
                    next == REGISTRATION_COMPENSATED;

            case SEAT_RELEASING ->
                    next == REGISTRATION_COMPENSATED;

            case PAYMENT_REQUIRED ->
                    next == PAYMENT_SUCCESS
                            || next == PAYMENT_FAILED;

            case PAYMENT_SUCCESS ->
                    next == REGISTRATION_COMPLETED;

            case PAYMENT_FAILED ->
                    next == SEAT_RELEASING;

            case REGISTRATION_COMPLETED,
                 REGISTRATION_COMPENSATED ->
                    false;
        };
    }

    // ...
}
