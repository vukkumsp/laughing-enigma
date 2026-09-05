package com.laughingenigma.payment_service.publisher;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.PaymentOrderResponse;
import com.laughingenigma.payment_service.dto.PaymentVerifyResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentOrderResponsePublisher {
    private final RabbitTemplate rabbitTemplate;

    public PaymentOrderResponsePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(PaymentOrderResponse response){
        System.out.println("PaymentOrderResponseConsumer response - " + response);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.PAYMENT_ORDER_RESPONSE_ROUTING_KEY,
                response
        );
    }
}