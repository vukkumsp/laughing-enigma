package com.laughingenigma.event_service.service;

import com.laughingenigma.event_service.config.RabbitMQConfig;
import com.laughingenigma.event_service.dto.SeatUnreserveRequest;
import com.laughingenigma.event_service.dto.SeatUnreserveResponse;
import com.laughingenigma.event_service.entity.*;
import com.laughingenigma.event_service.repository.OutboxMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Service
public class SeatUnreserveApplicationService {

    private final EventService eventService;
    private final ObjectMapper objectMapper;
    private OutboxMessageRepository outboxMessageRepository;

    public SeatUnreserveApplicationService(
            EventService eventService,
            ObjectMapper objectMapper,
            OutboxMessageRepository outboxMessageRepository
    ){
        this.eventService = eventService;
        this.objectMapper = objectMapper;
        this.outboxMessageRepository = outboxMessageRepository;
    }

    @Transactional
    public void handleSeatUnreserve(SeatUnreserveRequest request){
        boolean success = false;
        Event reservedEvent = eventService.releaseSeat(
                request.registrationId(),
                request.eventId()
        );
        success = true;
        SeatUnreserveResponse seatUnreserveResponse = new SeatUnreserveResponse(
                request.registrationId(),
                request.eventId(),
                success
        );
        System.out.println("handleSeatUnreserveRequest - "+request.registrationId());

        //TODO use outbox pattern
        //publisher.publish(seatUnreserveResponse);

        String payload = objectMapper.writeValueAsString(seatUnreserveResponse);

        OutboxMessage outboxMessage = OutboxMessage.builder()
                .outboxMessageId(UUID.randomUUID())
                .destinationType(DestinationType.RABBITMQ)
                .messageType(OutboxMessageType.SEAT_UNRESERVE_RESPONSE)
                .destinationName(RabbitMQConfig.SAGA_RESPONSE_EXCHANGE)
                .routingKey(RabbitMQConfig.SEAT_UNRESERVE_RESPONSE_ROUTING_KEY)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .retryCount(0)
                .nextRetryAt(null)
                .build();

        outboxMessageRepository.save(outboxMessage);
    }
}
