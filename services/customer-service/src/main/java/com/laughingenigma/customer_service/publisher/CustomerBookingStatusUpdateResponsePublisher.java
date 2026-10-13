package com.laughingenigma.customer_service.publisher;

import com.laughingenigma.customer_service.config.RabbitMQConfig;
import com.laughingenigma.customer_service.dto.CustomerBookingStatusUpdateRequest;
import com.laughingenigma.customer_service.dto.CustomerBookingStatusUpdateResponse;
import com.laughingenigma.customer_service.dto.CustomerValidationResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerBookingStatusUpdateResponsePublisher {

    private final RabbitTemplate rabbitTemplate;

    public CustomerBookingStatusUpdateResponsePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(CustomerBookingStatusUpdateResponse response) {
        System.out.println("CustomerValidationRequestConsumer CustomerValidationResponse - " + response);
        System.out.println("handleCustomerValidationRequest - "+response.registrationId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.CUSTOMER_BOOKING_STATUS_UPDATE_RESPONSE_ROUTING_KEY,
                response
        );
    }
}
