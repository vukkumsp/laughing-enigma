package com.laughingenigma.saga_orchestrator.saga;

import com.laughingenigma.saga_orchestrator.client.CustomerServiceClient;
import com.laughingenigma.saga_orchestrator.client.EventServiceClient;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationResponse;
import com.laughingenigma.saga_orchestrator.dto.RegistrationResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrationSaga {

    private final CustomerServiceClient customerServiceClient;
    private final EventServiceClient eventServiceClient;

    public RegistrationSaga(CustomerServiceClient customerServiceClient, EventServiceClient eventServiceClient) {
        this.customerServiceClient = customerServiceClient;
        this.eventServiceClient = eventServiceClient;
    }

    public RegistrationResponse startRegistration(
            Long eventId,
            String username) {

        String registrationId = UUID.randomUUID().toString();

        //Step 1: Validate Customer
        CustomerValidationResponse customer =
                customerServiceClient.validateCustomer(username);

        if (!customer.valid()) {
            return new RegistrationResponse(
                    registrationId,
                    eventId,
                    "FAILED"
            );
        }

        //Step 2: Reserve Seat
        try {
            eventServiceClient.reserveSeat(eventId);

            // Simulate a later step failing
//            throw new RuntimeException("Simulated failure");

        } catch (Exception ex) {

            // Compensation
            eventServiceClient.releaseSeat(eventId);

            return new RegistrationResponse(
                    registrationId,
                    eventId,
                    "FAILED"
            );
        }

        return new RegistrationResponse(
                registrationId,
                eventId,
                "SEAT_RESERVED"
        );
    }
}
