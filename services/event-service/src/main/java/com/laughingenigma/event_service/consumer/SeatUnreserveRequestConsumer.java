package com.laughingenigma.event_service.consumer;

import com.laughingenigma.event_service.config.RabbitMQConfig;
import com.laughingenigma.event_service.dto.SeatReservationRequest;
import com.laughingenigma.event_service.dto.SeatReservationResponse;
import com.laughingenigma.event_service.entity.Event;
import com.laughingenigma.event_service.publisher.SeatReservationResponsePublisher;
import com.laughingenigma.event_service.service.EventService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SeatReservationRequestConsumer {

    private final EventService eventService;
    private final SeatReservationResponsePublisher publisher;

    public SeatReservationRequestConsumer(
            EventService eventService, SeatReservationResponsePublisher publisher) {
        this.eventService = eventService;
        this.publisher = publisher;
    }

    @RabbitListener(
            queues = RabbitMQConfig.SEAT_RESERVATION_REQUEST_QUEUE
    )
    public void handleSeatReservationRequest(
            SeatReservationRequest request) {

        System.out.println("SeatReservationRequestConsumer SeatReservationRequest - " + request);
        boolean success = false;

        try{
            Event reservedEvent = eventService.reserveSeat(request.eventId());
            success = true;
            SeatReservationResponse seatReservationResponse = new SeatReservationResponse(
                    request.registrationId(),
                    request.eventId(),
                    request.customerId(),
                    reservedEvent.getPrice(),
                    reservedEvent.getCurrency(),
                    success
            );
            System.out.println("handleSeatReservationRequest - "+request.registrationId());

            publisher.publish(seatReservationResponse);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
