package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.PaymentFailureResponse;
import com.laughingenigma.saga_orchestrator.dto.PaymentVerifyResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import com.laughingenigma.saga_orchestrator.service.SseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/*
SAGA Compensation Step
 */
@Component
public class PaymentFailureResponseConsumer {
    private final RegistrationSaga registrationSaga;
    private final SagaInstanceRepository sagaInstanceRepository;

    public PaymentFailureResponseConsumer(
            RegistrationSaga registrationSaga,
            SagaInstanceRepository sagaInstanceRepository) {
        this.registrationSaga = registrationSaga;
        this.sagaInstanceRepository = sagaInstanceRepository;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_FAILURE_RESPONSE_QUEUE
    )
    public void handlePaymentFailureResponse(PaymentFailureResponse response){
        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId()).orElseThrow();

        System.out.println("Payment Failure response: "+response);
        System.out.println("Payment Failure Status: "+response);

        sagaI.setCurrentStep(SagaStep.PAYMENT_FAILED);
        sagaI.setStatus(SagaStatus.FAILED);
        sagaInstanceRepository.save(sagaI);

        //compensation step
        registrationSaga.unreserveSeatsAsCompensation(response);

    }
}
