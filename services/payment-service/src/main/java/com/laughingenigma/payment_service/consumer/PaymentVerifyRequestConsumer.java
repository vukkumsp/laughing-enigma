package com.laughingenigma.payment_service.consumer;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.PaymentOrderRequest;
import com.laughingenigma.payment_service.dto.PaymentOrderResponse;
import com.laughingenigma.payment_service.dto.PaymentVerifyRequest;
import com.laughingenigma.payment_service.dto.PaymentVerifyResponse;
import com.laughingenigma.payment_service.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class PaymentVerifyRequestConsumer {

    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;

    public PaymentVerifyRequestConsumer(
            PaymentService paymentService,
            RabbitTemplate rabbitTemplate) {
        this.paymentService = paymentService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_ORDER_REQUEST_QUEUE
    )
    public void handlePaymentVerifyRequest(
            PaymentVerifyRequest request) {

        boolean success = false;

        try{
            // Verify payment signature and other validations
        }
        catch (Exception e){
            e.printStackTrace();
        }

        PaymentVerifyResponse paymentVerifyResponse = new PaymentVerifyResponse(
                // "Update this based on DB or other ops status"
        );
        System.out.println("handlePaymentVerifyRequest - "+request.registrationId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.PAYMENT_VERIFY_RESPONSE_ROUTING_KEY,
                paymentVerifyResponse
        );
    }
}
