package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationResponse;
import com.laughingenigma.saga_orchestrator.dto.SeatReservationRequest;
import com.laughingenigma.saga_orchestrator.dto.SeatReservationResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SeatReservationResponseConsumer {
    private final RegistrationSaga registrationSaga;
    private final SagaInstanceRepository sagaInstanceRepository;

    public SeatReservationResponseConsumer(
            RegistrationSaga registrationSaga,
            SagaInstanceRepository sagaInstanceRepository) {
        this.registrationSaga = registrationSaga;
        this.sagaInstanceRepository = sagaInstanceRepository;
    }

    @RabbitListener(
            queues = RabbitMQConfig.SEAT_RESERVATION_RESPONSE_QUEUE
    )
    public void handleSeatReservationResponse(SeatReservationResponse response){
        System.out.println("Reservation response: "+response);
        System.out.println("Reservation Status: "+response.success());
        registrationSaga.initiatePaymentOrder(response);
    }
}
