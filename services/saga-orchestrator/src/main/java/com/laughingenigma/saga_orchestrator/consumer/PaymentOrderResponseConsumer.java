package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.PaymentOrderResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.ContextFactory;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.RegistrationSaga;
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
        SagaInstance sagaI = sagaInstanceRepository
                .findByCorrelationId(response.registrationId())
                .orElseThrow();

        if (sagaI.getCurrentStep() == SagaStep.PAYMENT_REQUIRED) {
            System.out.println(
                    "IDEMPOTENCY: Duplicate payment order response ignored. "
                            + "registrationId=" + response.registrationId()
            );
            return;
        }

        if (!sagaI.getCurrentStep()
                .canTransitionTo(SagaStep.PAYMENT_REQUIRED)) {
            // Duplicate / stale / invalid response
            System.out.println(
                    "SAGA TRANSITION REJECTED: "
                            + sagaI.getCurrentStep()
                            + " -> "
                            + SagaStep.PAYMENT_REQUIRED
                            + ", registrationId="
                            + response.registrationId()
            );
            return;
        }

        System.out.println("Payment Order response: "+response);

        sagaI.setCurrentStep(SagaStep.PAYMENT_REQUIRED);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaI.setContext(ContextFactory.buildPaymentOrderResponseContext(response));
        sagaInstanceRepository.save(sagaI);

        // TODO: Later revisit reliable delivery of the
        // PAYMENT_REQUIRED notification.

        sseService.sendPaymentRequiredEvent(response);
    }

}
