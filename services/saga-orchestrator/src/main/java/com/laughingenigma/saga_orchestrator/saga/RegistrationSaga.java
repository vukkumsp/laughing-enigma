package com.laughingenigma.saga_orchestrator.saga;

import com.laughingenigma.saga_orchestrator.dto.*;
import com.laughingenigma.saga_orchestrator.publisher.CustomerValidationRequestPublisher;
import com.laughingenigma.saga_orchestrator.publisher.PaymentOrderRequestPublisher;
import com.laughingenigma.saga_orchestrator.publisher.PaymentVerifyRequestPublisher;
import com.laughingenigma.saga_orchestrator.publisher.SeatReservationRequestPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class RegistrationSaga {

    private final CustomerValidationRequestPublisher customerValidationRequestPublisher;
    private final SeatReservationRequestPublisher seatReservationRequestPublisher;
    private final PaymentOrderRequestPublisher  paymentOrderRequestPublisher;
    private final PaymentVerifyRequestPublisher paymentVerifyRequestPublisher;

    public RegistrationSaga(
            CustomerValidationRequestPublisher customerValidationRequestPublisher,
            SeatReservationRequestPublisher seatReservationRequestPublisher,
            PaymentOrderRequestPublisher paymentOrderRequestPublisher,
            PaymentVerifyRequestPublisher paymentVerifyRequestPublisher) {
        this.customerValidationRequestPublisher = customerValidationRequestPublisher;
        this.seatReservationRequestPublisher = seatReservationRequestPublisher;
        this.paymentOrderRequestPublisher = paymentOrderRequestPublisher;
        this.paymentVerifyRequestPublisher = paymentVerifyRequestPublisher;
    }

    public RegistrationResponse startRegistration(
            String registrationId,
            Long eventId,
            String username) {

        //Step 1: Validate Customer
        CustomerValidationRequest customerValidationRequest =
                new CustomerValidationRequest(
                        registrationId,
                        username,
                        eventId
                );

        customerValidationRequestPublisher.publish(customerValidationRequest);

        System.out.println("startRegistration - "+registrationId);
        return new RegistrationResponse(
                registrationId,
                eventId,
                "STARTED"
        );
    }

    public void reserveSeatsForRegistration(
            CustomerValidationResponse response) {

        if (!response.valid()) {
            // Saga failed
//            handleRegistrationFailure(response);
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
        System.out.println("reserveSeatsForRegistration - "+response.registrationId());
    }

    public void initiatePaymentOrder(SeatReservationResponse response) {
        if (!response.success()) {
            // Saga failed
            return;
        }

        PaymentOrderRequest paymentOrderRequest = new  PaymentOrderRequest(
                response.registrationId(),
                response.eventId(),
                response.customerId(),
                response.price(),
                response.currency()
        );

        paymentOrderRequestPublisher.publish(paymentOrderRequest);
    }

    public void verifyPaymentOrder(PaymentVerifyRequest request) {
        //
        paymentVerifyRequestPublisher.publish(request);
    }
}
