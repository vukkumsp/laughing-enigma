package com.laughingenigma.saga_orchestrator.consumer;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.PaymentOrderResponse;
import com.laughingenigma.saga_orchestrator.dto.SeatReservationResponse;
import com.laughingenigma.saga_orchestrator.saga.RegistrationSaga;
import com.laughingenigma.saga_orchestrator.service.SseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentOrderResponseConsumer {
    private final RegistrationSaga registrationSaga;
    private final SseService  sseService;

    public PaymentOrderResponseConsumer(
            RegistrationSaga registrationSaga,
            SseService sseService) {
        this.registrationSaga = registrationSaga;
        this.sseService = sseService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_ORDER_RESPONSE_QUEUE
    )
    public void handlePaymentOrderResponse(PaymentOrderResponse response){
        System.out.println("Payment Order response: "+response);

        //IF payment order is successful then,
        //send SSE event to frontend for payment completion
        sseService.sendPaymentRequiredEvent(response);
    }

}
