package com.laughingenigma.saga_orchestrator.service;

import com.laughingenigma.saga_orchestrator.client.CustomerServiceClient;
import com.laughingenigma.saga_orchestrator.dto.CustomerValidationResponse;
import com.laughingenigma.saga_orchestrator.dto.RegistrationResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrationService {

    private final CustomerServiceClient customerServiceClient;

    public RegistrationService(CustomerServiceClient customerServiceClient) {
        this.customerServiceClient = customerServiceClient;
    }

    public RegistrationResponse startRegistration(
            Long eventId,
            String username) {

        String registrationId = UUID.randomUUID().toString();

        CustomerValidationResponse customer =
                customerServiceClient.validateCustomer(username);

        if (!customer.valid()) {
            return new RegistrationResponse(
                    registrationId,
                    eventId,
                    "FAILED"
            );
        }

        return new RegistrationResponse(
                registrationId,
                eventId,
                "VALIDATED_PENDING"
        );
    }
}
