package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.CustomerBookingStatusUpdateRequest;
import com.laughingenigma.saga_orchestrator.dto.PaymentOrderResponse;
import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import com.laughingenigma.saga_orchestrator.entity.SagaStatus;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.publisher.CustomerBookingsStatusUpdateRequestPublisher;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.ContextFactory;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.RegistrationSaga;
import com.laughingenigma.saga_orchestrator.service.sse.SseService;
import com.laughingenigma.saga_orchestrator.service.sse.dto.PaymentRequiredDetails;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class PaymentOrderResponseConsumer {
    private final RegistrationSaga registrationSaga;
    private final SseService  sseService;
    private final SagaInstanceRepository sagaInstanceRepository;

    private final CustomerBookingsStatusUpdateRequestPublisher publisher;

    public PaymentOrderResponseConsumer(
            RegistrationSaga registrationSaga,
            SseService sseService,
            SagaInstanceRepository sagaInstanceRepository,

            CustomerBookingsStatusUpdateRequestPublisher publisher) {
        this.registrationSaga = registrationSaga;
        this.sseService = sseService;
        this.sagaInstanceRepository = sagaInstanceRepository;

        this.publisher = publisher;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_ORDER_RESPONSE_QUEUE
    )
    public void handlePaymentOrderResponse(PaymentOrderResponse response){
        System.out.println("PaymentOrder response: "+response);
        System.out.println("PaymentOrder Status: "+response.status());
        registrationSaga.checkPaymentOrderAndUpdateCustomerBooking(response);
    }

}
