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
import com.laughingenigma.event_service.service.SeatUnreserveApplicationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SeatUnreserveRequestConsumer {

    private final EventService eventService;
    private final SeatUnreserveResponsePublisher publisher;
    private final SeatUnreserveApplicationService  seatUnreserveApplicationService;

    public SeatUnreserveRequestConsumer(
            EventService eventService, SeatUnreserveResponsePublisher publisher,
            SeatUnreserveApplicationService seatUnreserveApplicationService) {
        this.eventService = eventService;
        this.publisher = publisher;
        this.seatUnreserveApplicationService = seatUnreserveApplicationService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.SEAT_UNRESERVE_REQUEST_QUEUE
    )
    public void handleSeatUnreserveRequest(SeatUnreserveRequest request) {
        System.out.println("SeatUnreserveRequestConsumer SeatUnreserveRequest - " + request);
        seatUnreserveApplicationService.handleSeatUnreserve(request);
    }
}
