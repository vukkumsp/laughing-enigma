package com.laughingenigma.aggregator_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CustomerServiceClient {

    private final RestClient restClient;

    public CustomerServiceClient(
            @Value("${customer.service}") String customerServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(customerServiceUrl)
                .build();
    }

    public String validateCustomer(String username) {
        return restClient.get()
                .uri("/customers/me")
                .header("X-Authenticated-User", username)
                .retrieve()
                .body(String.class);
    }
}
