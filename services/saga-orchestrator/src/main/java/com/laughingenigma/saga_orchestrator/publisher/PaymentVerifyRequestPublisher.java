package com.laughingenigma.saga_orchestrator.publisher;

import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.PaymentVerifyRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentVerifyRequestPublisher {
    private final RabbitTemplate rabbitTemplate;

    public PaymentVerifyRequestPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(PaymentVerifyRequest paymentVerifyRequest) {
        System.out.println("Publishing paymentVerifyRequest - "+paymentVerifyRequest);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_COMMAND_EXCHANGE,
                RabbitMQConfig.PAYMENT_VERIFY_REQUEST_ROUTING_KEY,
                paymentVerifyRequest
        );
    }
}
