package com.laughingenigma.saga_orchestrator.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(
            @Value("${event.service}") String eventServiceUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(eventServiceUrl)
                .build();
    }

    public void reserveSeat(Long eventId) {
        restClient.post()
                .uri("/events/{eventId}/reserve", eventId)
                .retrieve()
                .toBodilessEntity();
    }

    public void releaseSeat(Long eventId) {
        restClient.post()
                .uri("/events/{eventId}/release", eventId)
                .retrieve()
                .toBodilessEntity();
    }
}
