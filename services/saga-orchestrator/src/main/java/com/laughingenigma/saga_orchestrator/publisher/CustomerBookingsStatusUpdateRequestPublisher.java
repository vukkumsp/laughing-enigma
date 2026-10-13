package com.laughingenigma.saga_orchestrator.publisher;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.CustomerBookingStatusUpdateRequest;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerBookingsStatusUpdateRequestPublisher {

    private final RabbitTemplate rabbitTemplate;

    public CustomerBookingsStatusUpdateRequestPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(CustomerBookingStatusUpdateRequest customerBookingStatusUpdateRequest) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_COMMAND_EXCHANGE,
                RabbitMQConfig.CUSTOMER_BOOKING_STATUS_UPDATE_REQUEST_ROUTING_KEY,
                customerBookingStatusUpdateRequest
        );
    }
}
