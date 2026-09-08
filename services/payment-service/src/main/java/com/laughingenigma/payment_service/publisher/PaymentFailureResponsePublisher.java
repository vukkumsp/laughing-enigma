package com.laughingenigma.payment_service.publisher;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.PaymentVerifyResponse;
import com.laughingenigma.payment_service.dto.event.PaymentFailureResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailureResponsePublisher {
    private final RabbitTemplate rabbitTemplate;

    public PaymentFailureResponsePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(PaymentFailureResponse paymentFailureResponse){
        System.out.println("PaymentFailureResponsePublisher paymentFailureResponse - " + paymentFailureResponse);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.PAYMENT_FAILURE_RESPONSE_ROUTING_KEY,
                paymentFailureResponse
        );
    }
}