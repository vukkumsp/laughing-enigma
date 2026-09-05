package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.PaymentOrderResponse;
import com.laughingenigma.saga_orchestrator.dto.PaymentVerifyResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import com.laughingenigma.saga_orchestrator.service.SseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentVerifyResponseConsumer {
    private final RegistrationSaga registrationSaga;
    private final SseService sseService;
    private final SagaInstanceRepository sagaInstanceRepository;

    public PaymentVerifyResponseConsumer(
            RegistrationSaga registrationSaga,
            SseService sseService,
            SagaInstanceRepository sagaInstanceRepository) {
        this.registrationSaga = registrationSaga;
        this.sseService = sseService;
        this.sagaInstanceRepository = sagaInstanceRepository;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_VERIFY_RESPONSE_QUEUE
    )
    public void handlePaymentVerifyResponse(PaymentVerifyResponse response){
        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId()).orElseThrow();

        System.out.println("Payment Verify response: "+response);
        System.out.println("Payment Verify Status: "+response);

        if(!response.status().equalsIgnoreCase("success")) {
            sagaI.setCurrentStep(SagaStep.PAYMENT_FAILED);
            sagaI.setStatus(SagaStatus.IN_PROGRESS);
            sagaInstanceRepository.save(sagaI);
            return;
        }

        sagaI.setCurrentStep(SagaStep.PAYMENT_SUCCESS);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaInstanceRepository.save(sagaI);

        //IF payment verification is successful then,
        //update status in saga db if needed
        //send SSE event to frontend payment status
        sseService.sendPaymentStatusEvent(response);

        //End of SAGA
        sagaI.setCurrentStep(SagaStep.REGISTRATION_COMPLETED);
        sagaI.setStatus(SagaStatus.COMPLETED);
        sagaInstanceRepository.save(sagaI);
    }
}
