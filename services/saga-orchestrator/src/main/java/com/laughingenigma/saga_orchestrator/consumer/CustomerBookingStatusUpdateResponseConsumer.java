package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.CustomerBookingStatusUpdateResponse;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.RegistrationSaga;
import com.laughingenigma.saga_orchestrator.service.sse.SseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerBookingStatusUpdateResponseConsumer {
    private final RegistrationSaga registrationSaga;


    public CustomerBookingStatusUpdateResponseConsumer(
            RegistrationSaga registrationSaga) {
        this.registrationSaga = registrationSaga;

    }

    @RabbitListener(
            queues = RabbitMQConfig.CUSTOMER_BOOKING_STATUS_UPDATE_RESPONSE_QUEUE
    )
    public void handleCustomerBookingStatusUpdateResponse(CustomerBookingStatusUpdateResponse response){
        System.out.println("Customer Booking response: "+response);
        System.out.println("Customer Booking Status: "+response.bookingStatus());

        registrationSaga.handleCustomerBookingStatusUpdateResponse(response);
    }
}
