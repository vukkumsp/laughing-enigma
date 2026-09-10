package com.laughingenigma.saga_orchestrator.saga;

import com.laughingenigma.saga_orchestrator.config.KafkaConfig;
import com.laughingenigma.saga_orchestrator.dto.*;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.entity.SagaType;
import com.laughingenigma.saga_orchestrator.kafka.event.EventDetails;
import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationCompletedPayload;
import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationEvent;
import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationEventPayload;
import com.laughingenigma.saga_orchestrator.kafka.producer.KafkaRegistrationEventProducer;
import com.laughingenigma.saga_orchestrator.publisher.*;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RegistrationSaga {

    private final CustomerValidationRequestPublisher customerValidationRequestPublisher;
    private final SeatReservationRequestPublisher seatReservationRequestPublisher;
    private final SeatUnreserveRequestPublisher seatUnreserveRequestPublisher;
    private final PaymentOrderRequestPublisher  paymentOrderRequestPublisher;
    private final PaymentVerifyRequestPublisher paymentVerifyRequestPublisher;
    private final KafkaRegistrationEventProducer kafkaRegistrationEventProducer;

    private final SagaInstanceRepository sagaInstanceRepository;

    public RegistrationSaga(
            CustomerValidationRequestPublisher customerValidationRequestPublisher,
            SeatReservationRequestPublisher seatReservationRequestPublisher,
            SeatUnreserveRequestPublisher seatUnreserveRequestPublisher,
            PaymentOrderRequestPublisher paymentOrderRequestPublisher,
            PaymentVerifyRequestPublisher paymentVerifyRequestPublisher,
            SagaInstanceRepository sagaInstanceRepository,
            KafkaRegistrationEventProducer kafkaRegistrationEventProducer) {
        this.customerValidationRequestPublisher = customerValidationRequestPublisher;
        this.seatReservationRequestPublisher = seatReservationRequestPublisher;
        this.seatUnreserveRequestPublisher = seatUnreserveRequestPublisher;
        this.paymentOrderRequestPublisher = paymentOrderRequestPublisher;
        this.paymentVerifyRequestPublisher = paymentVerifyRequestPublisher;
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.kafkaRegistrationEventProducer = kafkaRegistrationEventProducer;
    }

    public RegistrationResponse startRegistration(
            String registrationId,
            Long eventId,
            String username) {

        sagaInstanceRepository.save(SagaInstance.builder()
                                                .correlationId(registrationId)
                                                .sagaType(SagaType.REGISTRATION)
                                                .currentStep(SagaStep.REGISTRATION_STARTED)
                                                .status(SagaStatus.STARTED)
                                                .build());

        //Step 1: Validate Customer
        CustomerValidationRequest customerValidationRequest =
                new CustomerValidationRequest(
                        registrationId,
                        username,
                        eventId
                );

        customerValidationRequestPublisher.publish(customerValidationRequest);

        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(registrationId).orElseThrow();
        sagaI.setCurrentStep(SagaStep.CUSTOMER_VALIDATION);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaInstanceRepository.save(sagaI);

        System.out.println("startRegistration - "+registrationId);
        return new RegistrationResponse(
                registrationId,
                eventId,
                SagaStep.CUSTOMER_VALIDATION.name()
        );
    }

    public void reserveSeatsForRegistration(
            CustomerValidationResponse response) {
        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId()).orElseThrow();

        if (!response.valid()) {
            // Saga failed
//            handleRegistrationFailure(response);
            sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId()).orElseThrow();
            sagaI.setCurrentStep(SagaStep.CUSTOMER_VALIDATION_FAILED);
            sagaI.setStatus(SagaStatus.IN_PROGRESS);
            sagaInstanceRepository.save(sagaI);
            return;
        }

        SeatReservationRequest seatReservationRequest =
                new SeatReservationRequest(
                        response.registrationId(),
                        response.username(),
                        response.customerId(),
                        response.eventId()
                );

        // Customer validation succeeded.
        // Start Step 2.
        seatReservationRequestPublisher.publish(seatReservationRequest);

        sagaI.setCurrentStep(SagaStep.SEAT_RESERVATION);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaInstanceRepository.save(sagaI);

        System.out.println("reserveSeatsForRegistration - "+response.registrationId());
    }

    public void initiatePaymentOrder(SeatReservationResponse response) {
        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId()).orElseThrow();
        if (!response.success()) {
            // Saga failed
            sagaI.setCurrentStep(SagaStep.SEAT_RESERVATION_FAILED);
            sagaI.setStatus(SagaStatus.IN_PROGRESS);
            sagaInstanceRepository.save(sagaI);
            return;
        }

        sagaI.setCurrentStep(SagaStep.SEAT_RESERVED);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaInstanceRepository.save(sagaI);

        PaymentOrderRequest paymentOrderRequest = new  PaymentOrderRequest(
                response.registrationId(),
                response.eventId(),
                response.customerId(),
                response.price(),
                response.currency()
        );

        paymentOrderRequestPublisher.publish(paymentOrderRequest);
    }

    public PaymentVerifyResponse verifyPaymentOrder(PaymentVerifyRequest request) {
        paymentVerifyRequestPublisher.publish(request);

        return new PaymentVerifyResponse(
                request.registrationId(),
                request.eventId(),
                SagaStep.PAYMENT_VERIFICATION_STARTED.name()
        );
    }

    public void unreserveSeatsAsCompensation(PaymentFailureResponse response) {
        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId()).orElseThrow();

        SeatUnreserveRequest seatUnreserveRequest =
                new SeatUnreserveRequest(
                        response.registrationId(),
                        response.eventId()
                );

        seatUnreserveRequestPublisher.publish(seatUnreserveRequest);

        sagaI.setCurrentStep(SagaStep.SEAT_RELEASING);
        sagaI.setStatus(SagaStatus.COMPENSATING);
        sagaInstanceRepository.save(sagaI);

        System.out.println("unreserveSeatsAsCompensation - "+response.registrationId());

        //send email notification
        UUID eventId = UUID.randomUUID();
        EventDetails eventDetails = new EventDetails(
                response.eventId(),
                "Event Name",
                Instant.now(),
                Instant.now()
        );
        RegistrationEventPayload payload = new RegistrationCompletedPayload(
                response.registrationId(),
                "test456",
                "test456@test.com",
                eventDetails
        );
        RegistrationEvent event = new RegistrationEvent(
                eventId,
                "RegistrationFailed",
                1,
                Instant.now(),
                KafkaConfig.APPLICATION_NAME,
                payload
        );
        sendEmailNotification(event);
    }

    public void sendEmailNotification(RegistrationEvent event){
        kafkaRegistrationEventProducer.publish(event);
    }
}
