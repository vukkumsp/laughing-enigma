package com.laughingenigma.customer_service.consumer;

import com.laughingenigma.customer_service.config.RabbitMQConfig;
import com.laughingenigma.customer_service.dto.CustomerBookingStatusUpdateRequest;
import com.laughingenigma.customer_service.dto.CustomerValidationRequest;
import com.laughingenigma.customer_service.service.CustomerApplicationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerBookingStatusUpdateRequestConsumer {
    private final CustomerApplicationService customerApplicationService;

    public CustomerBookingStatusUpdateRequestConsumer(
            CustomerApplicationService customerApplicationService) {
        this.customerApplicationService = customerApplicationService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.CUSTOMER_BOOKING_STATUS_UPDATE_REQUEST_QUEUE
    )
    public void handleCustomerBookingStatusUpdateRequest(
            CustomerBookingStatusUpdateRequest request) {
        System.out.println("CustomerBookingStatusUpdateRequestConsumer CustomerBookingStatusUpdateRequest - "
                + request);
        customerApplicationService.createOrUpdateCustomerBookingAndRespondToSaga(request);
    }
}
