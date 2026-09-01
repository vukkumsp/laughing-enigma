package com.laughingenigma.payment_service.consumer;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.PaymentOrderRequest;
import com.laughingenigma.payment_service.dto.PaymentOrderResponse;
import com.laughingenigma.payment_service.dto.PaymentRequest;
import com.laughingenigma.payment_service.dto.PaymentResponse;
import com.laughingenigma.payment_service.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentOrderRequestConsumer {

    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;

    public PaymentOrderRequestConsumer(
            PaymentService paymentService,
            RabbitTemplate rabbitTemplate) {
        this.paymentService = paymentService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_ORDER_REQUEST_QUEUE
    )
    public void handlePaymentOrderRequest(
            PaymentOrderRequest request) {

        boolean success = false;

        try{
            //trigger payment flow
            //success = paymentService.completePayment(request);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        PaymentOrderResponse paymentOrderResponse = new PaymentOrderResponse(
                request.registrationId(),
                request.eventId(),
                request.amount(),
                request.currency(),
                "Update this based on DB or other ops status"
        );
        System.out.println("handlePaymentOrderRequest - "+request.registrationId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.PAYMENT_ORDER_RESPONSE_ROUTING_KEY,
                paymentOrderResponse
        );
    }
}
