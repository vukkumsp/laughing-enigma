package com.laughingenigma.payment_service.publisher;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.PaymentVerifyResponse;
import com.laughingenigma.payment_service.error.exception.RetryableOutboxException;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.AmqpTimeoutException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentVerifyResponsePublisher {
    private final RabbitTemplate rabbitTemplate;

    public PaymentVerifyResponsePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(PaymentVerifyResponse paymentVerifyResponse){
        try {
            System.out.println(
                    "PaymentVerifyResponsePublisher - " +
                            paymentVerifyResponse
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                    RabbitMQConfig.PAYMENT_VERIFY_RESPONSE_ROUTING_KEY,
                    paymentVerifyResponse
            );

        } catch (AmqpConnectException |
                 AmqpIOException |
                 AmqpTimeoutException e) {

            throw new RetryableOutboxException(
                    "RabbitMQ publication failed",
                    e
            );
        }
    }
}
