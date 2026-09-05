package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationRequest;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.entity.SagaType;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
        if(response.valid()){
            SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId()).orElseThrow();
            sagaI.setCurrentStep(SagaStep.CUSTOMER_VALIDATED);
            sagaI.setStatus(SagaStatus.IN_PROGRESS);
            sagaInstanceRepository.save(sagaI);
        }

        registrationSaga.reserveSeatsForRegistration(response);
    }
}
