package com.laughingenigma.saga_orchestrator.client;

import com.laughingenigma.saga_orchestrator.dto.CustomerValidationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CustomerServiceClient {

    private final RestClient restClient;

    public CustomerServiceClient(@Value("${customer.service}") String customerServiceUrl) {
        System.out.println("Customer Service URL = " + customerServiceUrl);
        this.restClient = RestClient.builder()
                .baseUrl(customerServiceUrl)
                .build();
    }

    public CustomerValidationResponse validateCustomer(String username) {
        return restClient.get()
                .uri("/customers/me")
                .header("X-Authenticated-User", username)
                .retrieve()
                .body(CustomerValidationResponse.class);
    }
}
