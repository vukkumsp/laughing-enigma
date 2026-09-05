package com.laughingenigma.saga_orchestrator.saga;

import com.laughingenigma.saga_orchestrator.dto.*;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.entity.SagaType;
import com.laughingenigma.saga_orchestrator.publisher.CustomerValidationRequestPublisher;
import com.laughingenigma.saga_orchestrator.publisher.PaymentOrderRequestPublisher;
import com.laughingenigma.saga_orchestrator.publisher.PaymentVerifyRequestPublisher;
import com.laughingenigma.saga_orchestrator.publisher.SeatReservationRequestPublisher;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationSaga {

    private final CustomerValidationRequestPublisher customerValidationRequestPublisher;
    private final SeatReservationRequestPublisher seatReservationRequestPublisher;
    private final PaymentOrderRequestPublisher  paymentOrderRequestPublisher;
    private final PaymentVerifyRequestPublisher paymentVerifyRequestPublisher;

    private final SagaInstanceRepository sagaInstanceRepository;

    public RegistrationSaga(
            CustomerValidationRequestPublisher customerValidationRequestPublisher,
            SeatReservationRequestPublisher seatReservationRequestPublisher,
            PaymentOrderRequestPublisher paymentOrderRequestPublisher,
            PaymentVerifyRequestPublisher paymentVerifyRequestPublisher,
            SagaInstanceRepository sagaInstanceRepository) {
        this.customerValidationRequestPublisher = customerValidationRequestPublisher;
        this.seatReservationRequestPublisher = seatReservationRequestPublisher;
        this.paymentOrderRequestPublisher = paymentOrderRequestPublisher;
        this.paymentVerifyRequestPublisher = paymentVerifyRequestPublisher;
        this.sagaInstanceRepository = sagaInstanceRepository;
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
        //
        paymentVerifyRequestPublisher.publish(request);

        return new PaymentVerifyResponse(
                request.registrationId(),
                request.eventId(),
                SagaStep.PAYMENT_VERIFICATION_STARTED.name()
        );
    }
}
