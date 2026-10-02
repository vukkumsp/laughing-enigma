package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.RegistrationSaga;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidationResponseConsumer {

    private final RegistrationSaga registrationSaga;
    private final SagaInstanceRepository sagaInstanceRepository;

    public CustomerValidationResponseConsumer(
            RegistrationSaga registrationSaga,
            SagaInstanceRepository sagaInstanceRepository) {
        this.registrationSaga = registrationSaga;
        this.sagaInstanceRepository = sagaInstanceRepository;
    }

    @RabbitListener(
            queues = RabbitMQConfig.CUSTOMER_VALIDATION_RESPONSE_QUEUE
    )
    public void handleCustomerValidationResponse(CustomerValidationResponse response){
        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId()).orElseThrow();

        System.out.println("Customer Validation response: "+response);

        if (sagaI.getCurrentStep() == SagaStep.CUSTOMER_VALIDATED) {
            //Idempotency Check
            //This saga's customer validation is already done, so we might be reading a duplicate response
            //So, Ignore this.
            System.out.println(
                    "IDEMPOTENCY CHECK: Duplicate customer validation response ignored. "
                            + "registrationId=" + response.registrationId()
            );
            return;
        }

        if (!sagaI.getCurrentStep()
                .canTransitionTo(SagaStep.CUSTOMER_VALIDATED)) {
            // Duplicate / stale / invalid response
            System.out.println(
                    "SAGA TRANSITION REJECTED: "
                            + sagaI.getCurrentStep()
                            + " -> "
                            + SagaStep.CUSTOMER_VALIDATED
                            + ", registrationId="
                            + response.registrationId()
            );
            return;
        }

        if(response.valid()){
            sagaI.setCurrentStep(SagaStep.CUSTOMER_VALIDATED);
            sagaI.setStatus(SagaStatus.IN_PROGRESS);
            sagaInstanceRepository.save(sagaI);

            // TODO: Introduce Outbox Pattern to atomically coordinate
            // SagaInstance state change and RabbitMQ message publishing.
            registrationSaga.reserveSeatsForRegistration(response);
        }
    }
}
