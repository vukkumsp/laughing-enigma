package com.laughingenigma.saga_orchestrator.controller;

import com.laughingenigma.saga_orchestrator.dto.PaymentVerifyRequest;
import com.laughingenigma.saga_orchestrator.dto.PaymentVerifyResponse;
import com.laughingenigma.saga_orchestrator.dto.RegistrationRequest;
import com.laughingenigma.saga_orchestrator.dto.RegistrationResponse;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationSaga registrationSaga;

    public RegistrationController(RegistrationSaga registrationSaga) {
        this.registrationSaga = registrationSaga;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponse> register(
            @RequestBody RegistrationRequest request,
            @RequestHeader("X-Authenticated-User") String username) {

        RegistrationResponse response =
                registrationSaga.startRegistration(
                        request.registrationId(),
                        request.eventId(),
                        username
                );

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/payment/verify")
    public ResponseEntity<RegistrationResponse> verifyPayment(@RequestBody PaymentVerifyRequest request){
        registrationSaga.verifyPaymentOrder(request);

        RegistrationResponse response = new RegistrationResponse(
                request.registrationId(),
                request.eventId(),
                "VERIFICATION_STARTED"
        );

        return ResponseEntity.accepted().body(response);
    }
}
