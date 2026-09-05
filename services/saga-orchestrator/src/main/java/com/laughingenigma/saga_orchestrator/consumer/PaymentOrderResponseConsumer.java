package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.PaymentOrderResponse;
import com.laughingenigma.saga_orchestrator.dto.SeatReservationResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import com.laughingenigma.saga_orchestrator.service.SseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentOrderResponseConsumer {
    private final RegistrationSaga registrationSaga;
    private final SseService  sseService;
    private final SagaInstanceRepository sagaInstanceRepository;

    public PaymentOrderResponseConsumer(
            RegistrationSaga registrationSaga,
            SseService sseService,
            SagaInstanceRepository sagaInstanceRepository) {
        this.registrationSaga = registrationSaga;
        this.sseService = sseService;
        this.sagaInstanceRepository = sagaInstanceRepository;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_ORDER_RESPONSE_QUEUE
    )
    public void handlePaymentOrderResponse(PaymentOrderResponse response){
        System.out.println("Payment Order response: "+response);

        //IF payment order is successful then,
        //send SSE event to frontend for payment completion
        sseService.sendPaymentRequiredEvent(response);

        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId()).orElseThrow();
        sagaI.setCurrentStep(SagaStep.PAYMENT_REQUIRED);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaInstanceRepository.save(sagaI);
    }

}
