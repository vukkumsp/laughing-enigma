package com.laughingenigma.saga_orchestrator.saga;

import com.laughingenigma.saga_orchestrator.dto.CustomerValidationResponse;
import com.laughingenigma.saga_orchestrator.dto.RegistrationResponse;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationRequest;
import com.laughingenigma.saga_orchestrator.dto.SeatReservationRequest;
import com.laughingenigma.saga_orchestrator.publisher.CustomerValidationRequestPublisher;
import com.laughingenigma.saga_orchestrator.publisher.SeatReservationRequestPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrationSaga {

    private final CustomerValidationRequestPublisher customerValidationRequestPublisher;
    private final SeatReservationRequestPublisher seatReservationRequestPublisher;

    public RegistrationSaga(
            CustomerValidationRequestPublisher customerValidationRequestPublisher,
            SeatReservationRequestPublisher seatReservationRequestPublisher) {
        this.customerValidationRequestPublisher = customerValidationRequestPublisher;
        this.seatReservationRequestPublisher = seatReservationRequestPublisher;
    }

    public RegistrationResponse startRegistration(
            Long eventId,
            String username) {

        String registrationId = UUID.randomUUID().toString();

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
            //handleRegistrationFailure(response);
            return;
        }

        SeatReservationRequest seatReservationRequest =
                new SeatReservationRequest(
                        response.registrationId(),
                        response.username(),
                        response.eventId()
                );

        // Customer validation succeeded.
        // Start Step 2.
        seatReservationRequestPublisher.publish(seatReservationRequest);
        System.out.println("reserveSeatsForRegistration - "+response.registrationId());
    }
}
