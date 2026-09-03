package com.laughingenigma.saga_orchestrator.publisher;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.PaymentOrderRequest;
import com.laughingenigma.saga_orchestrator.dto.SeatReservationRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentOrderRequestPublisher {
    private final RabbitTemplate rabbitTemplate;

    public PaymentOrderRequestPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(PaymentOrderRequest paymentOrderRequest) {
        System.out.println("Publishing paymentOrderRequest - "+paymentOrderRequest);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_COMMAND_EXCHANGE,
                RabbitMQConfig.PAYMENT_ORDER_REQUEST_ROUTING_KEY,
                paymentOrderRequest
        );
    }
}
