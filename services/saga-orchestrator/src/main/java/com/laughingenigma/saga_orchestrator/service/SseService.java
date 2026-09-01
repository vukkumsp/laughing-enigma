package com.laughingenigma.saga_orchestrator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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

    public void sendPaymentRequiredEvent(String registrationId){
        SseEmitter emitter = emitters.get(registrationId);

        if (emitter == null) {
            System.out.println("No SSE connection for " + registrationId);
            return;
        }

        try {
            System.out.println("Sending PAYMENT_REQUIRED event to " + registrationId);
            emitter.send(
                    SseEmitter.event()
                            .name(SSE_EVENT.PAYMENT_REQUIRED.name())
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

    public void sendPaymentStatusEvent(String registrationId){
        SseEmitter emitter = emitters.get(registrationId);

        if (emitter == null) {
            System.out.println("No SSE connection for " + registrationId);
            return;
        }

        try {
            System.out.println("Sending PAYMENT_SUCCESS event to " + registrationId);
            emitter.send(
                    SseEmitter.event()
                            .name(SSE_EVENT.PAYMENT_SUCCESS.name())
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