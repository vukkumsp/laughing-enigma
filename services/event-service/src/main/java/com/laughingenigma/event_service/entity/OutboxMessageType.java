package com.laughingenigma.event_service.entity;

public enum OutboxMessageType {
    // RabbitMQ
    SEAT_RESERVATION_RESPONSE,
    SEAT_UNRESERVE_RESPONSE
}
