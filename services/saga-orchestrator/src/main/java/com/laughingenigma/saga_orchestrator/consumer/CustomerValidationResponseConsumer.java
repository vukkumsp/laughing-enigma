package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationRequest;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationResponse;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidationResponseConsumer {

    private final RegistrationSaga registrationSaga;

    public CustomerValidationResponseConsumer(
            RegistrationSaga registrationSaga) {
        this.registrationSaga = registrationSaga;
    }

    @RabbitListener(
            queues = RabbitMQConfig.CUSTOMER_VALIDATION_RESPONSE_QUEUE
    )
    public void handleCustomerValidationResponse(CustomerValidationResponse response){
        registrationSaga.reserveSeatsForRegistration(response);
    }
}
