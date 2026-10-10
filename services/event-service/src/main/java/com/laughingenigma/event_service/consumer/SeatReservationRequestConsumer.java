package com.laughingenigma.event_service.consumer;

import com.laughingenigma.event_service.config.RabbitMQConfig;
import com.laughingenigma.event_service.dto.SeatReservationRequest;
import com.laughingenigma.event_service.dto.SeatReservationResponse;
import com.laughingenigma.event_service.entity.*;
import com.laughingenigma.event_service.publisher.SeatReservationResponsePublisher;
import com.laughingenigma.event_service.repository.OutboxMessageRepository;
import com.laughingenigma.event_service.service.EventService;
import com.laughingenigma.event_service.service.SeatReservationApplicationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class SeatReservationRequestConsumer {

    private final SeatReservationApplicationService seatReservationApplicationService;
    private final EventService eventService;
    private final SeatReservationResponsePublisher publisher;
    private final ObjectMapper objectMapper;
    private final OutboxMessageRepository  outboxMessageRepository;

    public SeatReservationRequestConsumer(
            EventService eventService, SeatReservationResponsePublisher publisher,
            ObjectMapper objectMapper, OutboxMessageRepository outboxMessageRepository,
            SeatReservationApplicationService seatReservationApplicationService) {
        this.eventService = eventService;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
        this.outboxMessageRepository = outboxMessageRepository;
        this.seatReservationApplicationService = seatReservationApplicationService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.SEAT_RESERVATION_REQUEST_QUEUE
    )
    public void handleSeatReservationRequest(SeatReservationRequest request) {
        System.out.println("SeatReservationRequestConsumer SeatReservationRequest - " + request);
        seatReservationApplicationService.handleSeatReservation(request);
    }
}
