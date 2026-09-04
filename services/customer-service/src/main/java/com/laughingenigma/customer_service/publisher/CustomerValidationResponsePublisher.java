package com.laughingenigma.customer_service.publisher;

import com.laughingenigma.customer_service.config.RabbitMQConfig;
import com.laughingenigma.customer_service.dto.CustomerValidationRequest;
import com.laughingenigma.customer_service.dto.CustomerValidationResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidationResponsePublisher {

    private final RabbitTemplate rabbitTemplate;

    public CustomerValidationResponsePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(CustomerValidationResponse response) {
        System.out.println("CustomerValidationRequestConsumer CustomerValidationResponse - " + response);
        System.out.println("handleCustomerValidationRequest - "+response.registrationId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.CUSTOMER_VALIDATION_RESPONSE_ROUTING_KEY,
                response
        );
    }
}
