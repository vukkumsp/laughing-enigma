package com.laughingenigma.saga_orchestrator.publisher;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.SeatUnreserveRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SeatUnreserveRequestPublisher {

    private final RabbitTemplate rabbitTemplate;

    public SeatUnreserveRequestPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(SeatUnreserveRequest seatUnreserveRequest) {
        System.out.println("Publishing SeatUnreserveRequest - "+seatUnreserveRequest);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_COMMAND_EXCHANGE,
                RabbitMQConfig.SEAT_UNRESERVE_REQUEST_ROUTING_KEY,
                seatUnreserveRequest
        );
    }
}
