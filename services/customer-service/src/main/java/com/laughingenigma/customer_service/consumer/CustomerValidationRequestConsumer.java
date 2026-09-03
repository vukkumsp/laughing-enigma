package com.laughingenigma.customer_service.consumer;

import com.laughingenigma.customer_service.config.RabbitMQConfig;
import com.laughingenigma.customer_service.dto.CustomerValidationResponse;
import com.laughingenigma.customer_service.dto.CustomerValidationRequest;
import com.laughingenigma.customer_service.service.CustomerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidationRequestConsumer {

    private final CustomerService customerService;
    private final RabbitTemplate rabbitTemplate;

    public CustomerValidationRequestConsumer(CustomerService customerService, RabbitTemplate rabbitTemplate) {
        this.customerService = customerService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(
            queues = RabbitMQConfig.CUSTOMER_VALIDATION_REQUEST_QUEUE
    )
    public void handleCustomerValidationRequest(
            CustomerValidationRequest request) {

        boolean valid = customerService.validateCustomer(request.username());
        CustomerValidationResponse response = new CustomerValidationResponse(
                request.registrationId(), request.eventId(), valid, request.username());

        System.out.println("CustomerValidationRequestConsumer CustomerValidationRequest - " + request);
        System.out.println("CustomerValidationRequestConsumer CustomerValidationResponse - " + response);
        System.out.println("handleCustomerValidationRequest - "+request.registrationId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.CUSTOMER_VALIDATION_RESPONSE_ROUTING_KEY,
                response
        );
    }
}
