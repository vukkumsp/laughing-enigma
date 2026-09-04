package com.laughingenigma.customer_service.consumer;

import com.laughingenigma.customer_service.config.RabbitMQConfig;
import com.laughingenigma.customer_service.dto.CustomerValidationResponse;
import com.laughingenigma.customer_service.dto.CustomerValidationRequest;
import com.laughingenigma.customer_service.entity.Customer;
import com.laughingenigma.customer_service.publisher.CustomerValidationResponsePublisher;
import com.laughingenigma.customer_service.service.CustomerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidationRequestConsumer {

    private final CustomerService customerService;
    private final CustomerValidationResponsePublisher publisher;

    public CustomerValidationRequestConsumer(
            CustomerService customerService,
            CustomerValidationResponsePublisher publisher) {
        this.customerService = customerService;
        this.publisher = publisher;
    }

    @RabbitListener(
            queues = RabbitMQConfig.CUSTOMER_VALIDATION_REQUEST_QUEUE
    )
    public void handleCustomerValidationRequest(
            CustomerValidationRequest request) {
        System.out.println("CustomerValidationRequestConsumer CustomerValidationRequest - " + request);
        Customer customer = customerService.validateCustomer(request.username());
        boolean valid = (customer != null);

        CustomerValidationResponse response = new CustomerValidationResponse(
                request.registrationId(), request.eventId(), valid, valid ? customer.getId() : 0L, request.username());

        publisher.publish(response);
    }
}
