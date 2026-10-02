package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.KafkaConfig;
import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.PaymentOrderResponse;
import com.laughingenigma.saga_orchestrator.dto.PaymentVerifyResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.kafka.event.EventDetails;
import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationCompletedPayload;
import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationEvent;
import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationEventPayload;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.DtoFactory;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.RegistrationSaga;
import com.laughingenigma.saga_orchestrator.service.SseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class PaymentVerifyResponseConsumer {
    private final RegistrationSaga registrationSaga;
    private final SseService sseService;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final DtoFactory dtoFactory;

    public PaymentVerifyResponseConsumer(
            RegistrationSaga registrationSaga,
            SseService sseService,
            SagaInstanceRepository sagaInstanceRepository,
            DtoFactory dtoFactory) {
        this.registrationSaga = registrationSaga;
        this.sseService = sseService;
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.dtoFactory = dtoFactory;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_VERIFY_RESPONSE_QUEUE
    )
    public void handlePaymentVerifyResponse(PaymentVerifyResponse response){
        System.out.println("========== PAYMENT VERIFY RESPONSE ==========");
        System.out.println("response = " + response);
        System.out.println("registrationId = [" + response.registrationId() + "]");

        SagaInstance sagaI = sagaInstanceRepository
                .findByCorrelationId(response.registrationId())
                .orElseThrow();

        System.out.println("Payment Verify response: "+response);
        System.out.println("sagaI.getCurrentStep(): " + sagaI.getCurrentStep());
        System.out.println("sagaI.getContext(): "+ sagaI.getContext());

        // PaymentOrderResponse
        PaymentOrderResponse paymentOrderResponse;

        if (response.status().equalsIgnoreCase("success")) {
            //success but duplicated
            if (sagaI.getCurrentStep() == SagaStep.REGISTRATION_COMPLETED) {
                // duplicate
                System.out.println(
                        "IDEMPOTENCY: Duplicate payment success response ignored. "
                                + "registrationId=" + response.registrationId()
                );
                return;
            }

            if (!sagaI.getCurrentStep()
                    .canTransitionTo(SagaStep.PAYMENT_SUCCESS)) {
                // stale/invalid response
                System.out.println(
                        "SAGA TRANSITION REJECTED: "
                                + sagaI.getCurrentStep()
                                + " -> "
                                + SagaStep.PAYMENT_SUCCESS
                                + ", registrationId="
                                + response.registrationId()
                );
                return;
            }
            // process success
        }

        if(!response.status().equalsIgnoreCase("success")) {
            //failure flow
            if (sagaI.getCurrentStep() == SagaStep.PAYMENT_FAILED
                    || sagaI.getCurrentStep() == SagaStep.REGISTRATION_COMPENSATED) {

                System.out.println(
                        "IDEMPOTENCY: Duplicate payment failure response ignored. "
                                + "registrationId=" + response.registrationId()
                );
                return;
            }

            if (!sagaI.getCurrentStep()
                    .canTransitionTo(SagaStep.PAYMENT_FAILED)) {
                // Duplicate / stale / invalid response
                System.out.println(
                        "SAGA TRANSITION REJECTED: " +
                                sagaI.getCurrentStep() +
                                " -> " +
                                SagaStep.PAYMENT_FAILED +
                                ", registrationId=" +
                                response.registrationId()
                );
                return;
            }

            paymentOrderResponse
                    = (PaymentOrderResponse) dtoFactory.getRegistrationContextDto(
                    SagaStep.PAYMENT_FAILED ,
                    sagaI.getContext());

            sagaI.setCurrentStep(SagaStep.PAYMENT_FAILED);
            sagaI.setStatus(SagaStatus.FAILED);
            sagaInstanceRepository.save(sagaI);

            //compensation step
            registrationSaga.unreserveSeatsAsCompensation(paymentOrderResponse);

            sseService.sendPaymentStatusEvent(paymentOrderResponse);
            return;
        }

        paymentOrderResponse
                = (PaymentOrderResponse) dtoFactory.getRegistrationContextDto(
                SagaStep.PAYMENT_SUCCESS,
                sagaI.getContext());

        sagaI.setCurrentStep(SagaStep.PAYMENT_SUCCESS);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaInstanceRepository.save(sagaI);

        //IF payment verification is successful then,
        //update status in saga db if needed
        //send SSE event to frontend payment status
        sseService.sendPaymentStatusEvent(paymentOrderResponse);

        //End of SAGA
        sagaI.setCurrentStep(SagaStep.REGISTRATION_COMPLETED);
        sagaI.setStatus(SagaStatus.COMPLETED);
        sagaInstanceRepository.save(sagaI);

        //send email notification
        UUID kafkaEventId = UUID.randomUUID();

        EventDetails eventDetails = new EventDetails(
                paymentOrderResponse.eventId(),
                paymentOrderResponse.eventName(),
                paymentOrderResponse.eventDate().toInstant(ZoneOffset.UTC),
                Instant.now()
        );
        RegistrationEventPayload payload = new RegistrationCompletedPayload(
                paymentOrderResponse.registrationId(),
                paymentOrderResponse.username(),
                paymentOrderResponse.email(),
                eventDetails
        );
        RegistrationEvent event = new RegistrationEvent(
                kafkaEventId,
                KafkaConfig.REGISTRATION_COMPLETED,
                1,
                Instant.now(),
                KafkaConfig.APPLICATION_NAME,
                payload
        );

        // TODO: Introduce Outbox Pattern to reliably coordinate
        // saga completion and REGISTRATION_COMPLETED Kafka publication.
        registrationSaga.sendEmailNotification(event);
    }
}
