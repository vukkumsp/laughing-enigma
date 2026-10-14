package com.laughingenigma.aggregator_service.client;

import com.laughingenigma.aggregator_service.dto.CustomerBookingSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CustomerServiceClient {

    private final RestClient restClient;

    public CustomerServiceClient(
            @Value("${customer.service}") String customerServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(customerServiceUrl)
                .build();
    }

    public List<CustomerBookingSummary> getAllBookings(String username) {
        List<CustomerBookingSummary> bookings = restClient.get()
                .uri("/api/v1/customers/me/bookings")
                .header("X-Authenticated-User", username)
                .retrieve()
//                .body(String.class);
                .body(new ParameterizedTypeReference<List<CustomerBookingSummary>>() {});

        System.out.println("String username: " + username);
        System.out.println("List<CustomerBookingSummary> bookings: " + bookings);

        return bookings != null ? bookings : List.of();
    }
}
