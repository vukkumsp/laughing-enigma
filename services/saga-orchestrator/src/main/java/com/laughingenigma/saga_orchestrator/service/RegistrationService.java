package com.laughingenigma.saga_orchestrator.service;

import com.laughingenigma.saga_orchestrator.dto.RegistrationResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrationService {

    public RegistrationResponse startRegistration(
            Long eventId,
            String username) {

        String registrationId = UUID.randomUUID().toString();

        return new RegistrationResponse(
                registrationId,
                eventId,
                "PENDING"
        );
    }
}
