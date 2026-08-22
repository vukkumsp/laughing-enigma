package com.laughingenigma.event_service.consumer;

import com.laughingenigma.event_service.config.RabbitMQConfig;
import com.laughingenigma.event_service.dto.SeatReservationRequest;
import com.laughingenigma.event_service.dto.SeatReservationResponse;
import com.laughingenigma.event_service.entity.Event;
import com.laughingenigma.event_service.service.EventService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SeatReservationRequestConsumer {

    private final EventService eventService;
    private final RabbitTemplate rabbitTemplate;

    public SeatReservationRequestConsumer(
            EventService eventService, RabbitTemplate rabbitTemplate) {
        this.eventService = eventService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(
            queues = RabbitMQConfig.SEAT_RESERVATION_REQUEST_QUEUE
    )
    public void handleSeatReservationRequest(
            SeatReservationRequest request) {

        boolean success = false;

        try{
            eventService.reserveSeat(request.eventId());
            success = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        SeatReservationResponse seatReservationResponse = new SeatReservationResponse(
                request.registrationId(),
                request.eventId(),
                success
        );
        System.out.println("handleSeatReservationRequest - "+request.registrationId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.SEAT_RESERVATION_RESPONSE_ROUTING_KEY,
                seatReservationResponse
        );
    }
}
