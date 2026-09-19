package com.laughingenigma.aggregator_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentServiceClient {

    private final RestClient restClient;

    public PaymentServiceClient(
            @Value("${payment.service}") String eventServiceUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(eventServiceUrl)
                .build();
    }

    public String getPayment(String username) {
        return restClient.get()
                .uri("/customers/me")
                .header("X-Authenticated-User", username)
                .retrieve()
                .body(String.class);
    }
}
