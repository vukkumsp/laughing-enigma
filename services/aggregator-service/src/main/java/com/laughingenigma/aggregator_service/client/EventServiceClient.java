package com.laughingenigma.aggregator_service.client;

import com.laughingenigma.aggregator_service.dto.CustomerBookingSummary;
import com.laughingenigma.aggregator_service.dto.EventSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(
            @Value("${event.service}") String eventServiceUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(eventServiceUrl)
                .build();
    }

    public List<EventSummary> getAllEvents(List<Long> eventIds) {
        List<EventSummary> eventSummaryList = restClient.post()
                .uri("/api/v1/events/batch")
                .body(eventIds)
                .retrieve()
                .body(new ParameterizedTypeReference<List<EventSummary>>() {});

        System.out.println("List<Long> eventIds: " + eventIds);
        System.out.println("List<EventSummary> eventSummaryList: " + eventSummaryList);

        return eventSummaryList != null ? eventSummaryList : List.of();
    }
}
