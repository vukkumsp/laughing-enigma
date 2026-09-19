package com.laughingenigma.saga_orchestrator.saga.registration_saga;

import com.laughingenigma.saga_orchestrator.dto.PaymentOrderResponse;

import java.util.HashMap;
import java.util.Map;

public class ContextFactory {
    public static Map<String, Object> buildPaymentOrderResponseContext (PaymentOrderResponse response){
        Map<String, Object> context = new HashMap<>();
        context.put("registrationId", response.registrationId());
        context.put("eventId", response.eventId());
        context.put("customerId", response.customerId());
        context.put("username", response.username());
        context.put("email", response.email());
        context.put("firstName", response.firstName());
        context.put("lastName", response.lastName());
        context.put("eventName", response.eventName());
        context.put("eventDate", response.eventDate());
        context.put("price", response.price());
        context.put("currency", response.currency());
        context.put("orderId", response.orderId());
        context.put("status", response.status());
        return context;
    }
}
