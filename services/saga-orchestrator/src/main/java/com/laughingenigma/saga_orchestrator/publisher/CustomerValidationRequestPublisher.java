package com.laughingenigma.saga_orchestrator.publisher;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationRequest;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidationRequestPublisher {

    private final RabbitTemplate rabbitTemplate;

    public CustomerValidationRequestPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(CustomerValidationRequest customerValidationRequest) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_COMMAND_EXCHANGE,
                RabbitMQConfig.CUSTOMER_VALIDATION_REQUEST_ROUTING_KEY,
                customerValidationRequest
        );
    }
}
