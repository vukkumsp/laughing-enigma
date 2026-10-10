package com.laughingenigma.saga_orchestrator.entity;

public enum OutboxMessageType {
    // RabbitMQ
    CUSTOMER_VALIDATION_REQUEST,
    SEAT_RESERVATION_REQUEST,
    SEAT_UNRESERVE_REQUEST,
    PAYMENT_ORDER_REQUEST,
    //Kafka
    REGISTRATION_COMPLETED,
    REGISTRATION_FAILED
}
