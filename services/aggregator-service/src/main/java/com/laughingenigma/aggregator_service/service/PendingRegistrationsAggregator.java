package com.laughingenigma.aggregator_service.service;

import com.laughingenigma.aggregator_service.client.CustomerServiceClient;
import com.laughingenigma.aggregator_service.client.EventServiceClient;
import com.laughingenigma.aggregator_service.client.PaymentServiceClient;
import com.laughingenigma.aggregator_service.dto.PendingRegistrationsResponse;
import org.springframework.stereotype.Service;

@Service
public class PendingRegistrationsAggregator {
    private final EventServiceClient eventServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public PendingRegistrationsAggregator(
            EventServiceClient eventServiceClient,
            PaymentServiceClient paymentServiceClient
    ) {
        this.eventServiceClient = eventServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    public PendingRegistrationsResponse getPendingRegistrations(){

        return null;
    }
}
