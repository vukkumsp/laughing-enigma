package com.laughingenigma.event_service.service;

import com.laughingenigma.event_service.config.RabbitMQConfig;
import com.laughingenigma.event_service.dto.SeatReservationRequest;
import com.laughingenigma.event_service.dto.SeatReservationResponse;
import com.laughingenigma.event_service.entity.*;
import com.laughingenigma.event_service.repository.OutboxMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Service
public class SeatReservationApplicationService {

    private final EventService eventService;
    private final ObjectMapper objectMapper;
    private OutboxMessageRepository  outboxMessageRepository;

    public SeatReservationApplicationService(
            EventService eventService,
            ObjectMapper objectMapper,
            OutboxMessageRepository outboxMessageRepository
    ){
        this.eventService = eventService;
        this.objectMapper = objectMapper;
        this.outboxMessageRepository = outboxMessageRepository;
    }

    @Transactional
    public void handleSeatReservation(SeatReservationRequest request){
        boolean success = false;

        Event reservedEvent = eventService.reserveSeat(
                request.registrationId(),
                request.eventId()
        );
        success = true;
        SeatReservationResponse seatReservationResponse = new SeatReservationResponse(
                request.registrationId(),
                request.eventId(),

                request.customerId(),
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName(),

                reservedEvent.getName(),
                reservedEvent.getEventDate(),
                reservedEvent.getPrice(),
                reservedEvent.getCurrency(),
                success
        );
        System.out.println("handleSeatReservationRequest - "+request.registrationId());

        //publisher.publish(seatReservationResponse);

        String payload = objectMapper.writeValueAsString(seatReservationResponse);

        OutboxMessage outboxMessage = OutboxMessage.builder()
                .outboxMessageId(UUID.randomUUID())
                .destinationType(DestinationType.RABBITMQ)
                .messageType(OutboxMessageType.SEAT_RESERVATION_RESPONSE)
                .destinationName(RabbitMQConfig.SAGA_RESPONSE_EXCHANGE)
                .routingKey(RabbitMQConfig.SEAT_RESERVATION_RESPONSE_ROUTING_KEY)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .retryCount(0)
                .nextRetryAt(null)
                .build();

        outboxMessageRepository.save(outboxMessage);
    }
}
