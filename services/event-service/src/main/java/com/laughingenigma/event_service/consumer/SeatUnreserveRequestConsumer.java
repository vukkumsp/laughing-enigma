package com.laughingenigma.event_service.consumer;

import com.laughingenigma.event_service.config.RabbitMQConfig;
import com.laughingenigma.event_service.dto.SeatReservationRequest;
import com.laughingenigma.event_service.dto.SeatReservationResponse;
import com.laughingenigma.event_service.dto.SeatUnreserveRequest;
import com.laughingenigma.event_service.dto.SeatUnreserveResponse;
import com.laughingenigma.event_service.entity.Event;
import com.laughingenigma.event_service.publisher.SeatReservationResponsePublisher;
import com.laughingenigma.event_service.publisher.SeatUnreserveResponsePublisher;
import com.laughingenigma.event_service.service.EventService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SeatUnreserveRequestConsumer {

    private final EventService eventService;
    private final SeatUnreserveResponsePublisher publisher;

    public SeatUnreserveRequestConsumer(
            EventService eventService, SeatUnreserveResponsePublisher publisher) {
        this.eventService = eventService;
        this.publisher = publisher;
    }

    @RabbitListener(
            queues = RabbitMQConfig.SEAT_RESERVATION_REQUEST_QUEUE
    )
    public void handleSeatUnreserveRequest(SeatUnreserveRequest request) {

        System.out.println("SeatUnreserveRequestConsumer SeatUnreserveRequest - " + request);
        boolean success = false;

        try{
            Event reservedEvent = eventService.releaseSeat(request.eventId());
            success = true;
            SeatUnreserveResponse seatUnreserveResponse = new SeatUnreserveResponse(
                    request.registrationId(),
                    request.eventId(),
                    success
            );
            System.out.println("handleSeatUnreserveRequest - "+request.registrationId());

            publisher.publish(seatUnreserveResponse);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
