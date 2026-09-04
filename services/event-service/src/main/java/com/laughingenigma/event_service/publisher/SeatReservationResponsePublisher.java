package com.laughingenigma.event_service.publisher;

import com.laughingenigma.event_service.config.RabbitMQConfig;
import com.laughingenigma.event_service.dto.SeatReservationResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SeatReservationResponsePublisher {

    private final RabbitTemplate rabbitTemplate;

    public SeatReservationResponsePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(SeatReservationResponse seatReservationResponse){
        System.out.println("SeatReservationRequestConsumer SeatReservationResponse - " + seatReservationResponse);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.SEAT_RESERVATION_RESPONSE_ROUTING_KEY,
                seatReservationResponse
        );
    }
}
