package com.laughingenigma.saga_orchestrator.publisher;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationRequest;
import com.laughingenigma.saga_orchestrator.dto.SeatReservationRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SeatReservationRequestPublisher {

    private final RabbitTemplate rabbitTemplate;

    public SeatReservationRequestPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(SeatReservationRequest seatReservationRequest) {
        System.out.println("Publishing SeatReservationRequest - "+seatReservationRequest);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_COMMAND_EXCHANGE,
                RabbitMQConfig.SEAT_RESERVATION_REQUEST_ROUTING_KEY,
                seatReservationRequest
        );
    }
}
