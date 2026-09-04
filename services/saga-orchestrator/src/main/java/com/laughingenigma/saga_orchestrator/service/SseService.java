package com.laughingenigma.saga_orchestrator.service;

import com.laughingenigma.saga_orchestrator.dto.PaymentOrderResponse;
import com.laughingenigma.saga_orchestrator.dto.PaymentVerifyResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    private final Map<String, SseEmitter> emitters =
            new ConcurrentHashMap<>();

    public SseEmitter connect(String registrationId) {

        SseEmitter emitter =
                new SseEmitter(30 * 60 * 1000L);

        emitters.put(registrationId, emitter);

        emitter.onCompletion(
                () -> emitters.remove(registrationId)
        );

        emitter.onTimeout(() -> {
            emitters.remove(registrationId);
            emitter.complete();
        });

        emitter.onError(error ->
                emitters.remove(registrationId)
        );

        return emitter;
    }

    public void sendPaymentRequiredEvent(PaymentOrderResponse response){
        SseEmitter emitter = emitters.get(response.registrationId());

        if (emitter == null) {
            System.out.println("No SSE connection for " + response.registrationId());
            return;
        }

        try {
            System.out.println("Sending PAYMENT_REQUIRED event to " + response.registrationId());
            emitter.send(
                    SseEmitter.event()
                            .name(SSE_EVENT.PAYMENT_REQUIRED.name())
                            .data("""
                                {
                                  "message": "Complete the payment",
                                  "registrationId": "%s",
                                  "eventId": "%s",
                                  "orderId": "%s",
                                  "amount": %s,
                                  "currency": "%s"
                                }
                                """.formatted(
                                    response.registrationId(),
                                    response.eventId(),
                                    response.orderId(),
                                    response.amount(),
                                    response.currency()))
            );
        } catch (IOException e) {
            emitters.remove(response.registrationId());
            emitter.completeWithError(e);
        }
    }

    public void sendPaymentStatusEvent(PaymentVerifyResponse response){
        SseEmitter emitter = emitters.get(response.registrationId());

        if (emitter == null) {
            System.out.println("No SSE connection for " + response.registrationId());
            return;
        }

        try {
            System.out.println("Sending PAYMENT_SUCCESS event to " + response.registrationId());
            emitter.send(
                    SseEmitter.event()
                            .name(SSE_EVENT.PAYMENT_SUCCESS.name())
                            .data("""
                                {
                                  "message": "SSE is working",
                                  "registrationId": "%s",
                                  "eventId": "%s",
                                  "status": "%s"
                                }
                                """.formatted(
                                    response.registrationId(),
                                    response.eventId(),
                                    response.status()))
            );
        } catch (IOException e) {
            emitters.remove(response.registrationId());
            emitter.completeWithError(e);
        }
    }

    //test event send
    public void sendTestEvent(String registrationId) {

        SseEmitter emitter =
                emitters.get(registrationId);

        if (emitter == null) {
            System.out.println(
                    "No SSE connection for "
                            + registrationId
            );
            return;
        }

        try {
            System.out.println(
                    "Sending TEST event to " + registrationId
            );
            emitter.send(
                    SseEmitter.event()
                            .name("TEST")
                            .data("""
                                {
                                  "message": "SSE is working",
                                  "registrationId": "%s"
                                }
                                """.formatted(registrationId))
            );
        } catch (IOException e) {
            emitters.remove(registrationId);
            emitter.completeWithError(e);
        }
    }
}

enum SSE_EVENT {
    PAYMENT_REQUIRED,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED
}