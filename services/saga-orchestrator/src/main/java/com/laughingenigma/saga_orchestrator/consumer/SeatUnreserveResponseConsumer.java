package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.SeatUnreserveResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SeatUnreserveResponseConsumer {
    private final RegistrationSaga registrationSaga;
    private final SagaInstanceRepository sagaInstanceRepository;

    public SeatUnreserveResponseConsumer(
            RegistrationSaga registrationSaga,
            SagaInstanceRepository sagaInstanceRepository) {
        this.registrationSaga = registrationSaga;
        this.sagaInstanceRepository = sagaInstanceRepository;
    }

    @RabbitListener(
            queues = RabbitMQConfig.SEAT_UNRESERVE_RESPONSE_QUEUE
    )
    public void handleSeatUnreserveResponse(SeatUnreserveResponse response){
        System.out.println("SeatUnreserveResponse response: "+response);
        System.out.println("SeatUnreserveResponse Status: "+response.success());

        //End of SAGA
        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId()).orElseThrow();
        sagaI.setCurrentStep(SagaStep.SEAT_UNRESERVED);
        sagaI.setStatus(SagaStatus.COMPENSATED);
        sagaInstanceRepository.save(sagaI);
    }
}
