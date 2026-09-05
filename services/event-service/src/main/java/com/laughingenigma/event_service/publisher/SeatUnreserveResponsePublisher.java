package com.laughingenigma.event_service.publisher;

import com.laughingenigma.event_service.config.RabbitMQConfig;
import com.laughingenigma.event_service.dto.SeatReservationResponse;
import com.laughingenigma.event_service.dto.SeatUnreserveResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SeatUnreserveResponsePublisher {

    private final RabbitTemplate rabbitTemplate;

    public SeatUnreserveResponsePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(SeatUnreserveResponse seatUnreserveResponse){
        System.out.println("SeatUnreserveResponsePublisher SeatUnreserveResponse - " + seatUnreserveResponse);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.SEAT_RESERVATION_RESPONSE_ROUTING_KEY,
                seatUnreserveResponse
        );
    }
}
