package com.laughingenigma.saga_orchestrator.kafka.event;

import java.time.Instant;

public record EventDetails(
        Long eventId,
        String name,
        Instant startTime,
        Instant endTime
) {
}
