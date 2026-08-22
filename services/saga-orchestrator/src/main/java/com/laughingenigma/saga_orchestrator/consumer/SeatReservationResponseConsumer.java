package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationResponse;
import com.laughingenigma.saga_orchestrator.dto.SeatReservationRequest;
import com.laughingenigma.saga_orchestrator.dto.SeatReservationResponse;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SeatReservationResponseConsumer {
    private final RegistrationSaga registrationSaga;

    public SeatReservationResponseConsumer(
            RegistrationSaga registrationSaga) {
        this.registrationSaga = registrationSaga;
    }

    @RabbitListener(
            queues = RabbitMQConfig.SEAT_RESERVATION_RESPONSE_QUEUE
    )
    public void handleCustomerValidationResponse(SeatReservationResponse response){
        System.out.println("Reservation response: "+response);
        System.out.println("Reservation Status: "+response.success());
    }
}
