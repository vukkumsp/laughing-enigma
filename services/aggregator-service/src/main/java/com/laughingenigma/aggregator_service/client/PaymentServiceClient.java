package com.laughingenigma.aggregator_service.client;

import com.laughingenigma.aggregator_service.dto.PaymentSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class PaymentServiceClient {

    private final RestClient restClient;

    public PaymentServiceClient(
            @Value("${payment.service}") String eventServiceUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(eventServiceUrl)
                .build();
    }

    public List<PaymentSummary> getAllOrdersPending(Long customerId) {
        return restClient.get()
                .uri("/api/v1/payments/ordersPending?customerId={customerId}", customerId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<PaymentSummary>>() {});
    }
}
