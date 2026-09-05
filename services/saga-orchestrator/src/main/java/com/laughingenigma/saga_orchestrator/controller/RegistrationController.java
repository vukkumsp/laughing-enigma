package com.laughingenigma.saga_orchestrator.controller;

import com.laughingenigma.saga_orchestrator.dto.*;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
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
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(@RequestBody PaymentVerifyRequest request){
        PaymentVerifyResponse response = registrationSaga.verifyPaymentOrder(request);

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/payment/failed")
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(@RequestBody PaymentFailureRequest request){
        PaymentVerifyResponse response = registrationSaga.verifyPaymentOrder(request);

        return ResponseEntity.accepted().body(response);
    }
}
