package com.laughingenigma.aggregator_service.service;

import com.laughingenigma.aggregator_service.client.CustomerServiceClient;
import com.laughingenigma.aggregator_service.client.EventServiceClient;
import com.laughingenigma.aggregator_service.client.PaymentServiceClient;
import com.laughingenigma.aggregator_service.dto.CustomerBookingSummary;
import com.laughingenigma.aggregator_service.dto.EventSummary;
import com.laughingenigma.aggregator_service.dto.RegistrationsResponse;
import com.laughingenigma.aggregator_service.dto.RegistrationsSummary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RegistrationsAggregator {
    private final CustomerServiceClient customerServiceClient;
    private final EventServiceClient eventServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public RegistrationsAggregator(
            CustomerServiceClient customerServiceClient,
            EventServiceClient eventServiceClient,
            PaymentServiceClient paymentServiceClient
    ) {
        this.customerServiceClient = customerServiceClient;
        this.eventServiceClient = eventServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    public List<RegistrationsSummary> getRegistrations(String username){
        List<CustomerBookingSummary> bookings = customerServiceClient.getAllBookings(username);

        List<Long> eventIds = bookings.stream()
                .map(CustomerBookingSummary::eventId)
                .distinct()
                .toList();

        List<EventSummary> events = eventServiceClient.getAllEvents(eventIds);
        Map<Long, EventSummary> eventMap = events.stream()
                .collect(Collectors.toMap(
                        EventSummary::id,
                        Function.identity()
                ));

        List<RegistrationsSummary> registrationsSummaryList =
                bookings.stream()
                        .map(booking -> {
                            EventSummary eventSummary = eventMap.get(booking.eventId());

                            return new RegistrationsSummary(
                                    booking.customerUsername(),
                                    booking.registrationId(),

                                    booking.orderId(),
                                    booking.bookingStatus(),

                                    eventSummary.id(),
                                    eventSummary.name(),
                                    eventSummary.description(),
                                    eventSummary.eventDate(),
                                    eventSummary.availableSeats(),
                                    eventSummary.currency(),
                                    eventSummary.price()
                            );
                        }).toList();

        return registrationsSummaryList;
    }
}
