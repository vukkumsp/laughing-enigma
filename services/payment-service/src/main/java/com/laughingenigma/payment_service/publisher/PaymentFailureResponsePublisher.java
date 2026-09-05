package com.laughingenigma.payment_service.publisher;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.PaymentVerifyResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailureResponsePublisher {
    private final RabbitTemplate rabbitTemplate;

    public PaymentFailureResponsePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(PaymentVerifyResponse paymentVerifyResponse){
        System.out.println("PaymentVerifyRequestConsumer PaymentVerifyResponse - " + paymentVerifyResponse);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.PAYMENT_FAILURE_RESPONSE_ROUTING_KEY,
                paymentVerifyResponse
        );
    }
}