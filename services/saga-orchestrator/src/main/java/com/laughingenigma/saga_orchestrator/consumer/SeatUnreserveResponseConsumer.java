package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.SeatUnreserveResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.RegistrationSaga;
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
        SagaInstance sagaI = sagaInstanceRepository
                .findByCorrelationId(response.registrationId())
                .orElseThrow();

        if (sagaI.getCurrentStep() == SagaStep.REGISTRATION_COMPENSATED) {
            System.out.println(
                    "IDEMPOTENCY: Duplicate seat unreserve response ignored. "
                            + "registrationId=" + response.registrationId()
            );
            return;
        }

        if (!sagaI.getCurrentStep()
                .canTransitionTo(SagaStep.REGISTRATION_COMPENSATED)) {
            // Duplicate / stale / invalid response
            System.out.println(
                    "SAGA TRANSITION REJECTED: " +
                            sagaI.getCurrentStep() +
                            " -> " +
                            SagaStep.REGISTRATION_COMPENSATED +
                            ", registrationId=" +
                            response.registrationId()
            );
            return;
        }


        sagaI.setCurrentStep(SagaStep.REGISTRATION_COMPENSATED);
        sagaI.setStatus(SagaStatus.COMPENSATED);
        sagaInstanceRepository.save(sagaI);

        //SAGA COMPENSATED
        System.out.println(
                "SAGA COMPENSATED: registrationId="
                        + response.registrationId()
        );
    }
}
