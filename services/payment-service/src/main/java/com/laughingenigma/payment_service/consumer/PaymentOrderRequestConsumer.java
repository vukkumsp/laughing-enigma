package com.laughingenigma.payment_service.consumer;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.*;
import com.laughingenigma.payment_service.service.PaymentService;
import com.razorpay.Order;
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
        System.out.println("PaymentOrderRequestConsumer PaymentOrderRequest - " + request);

        boolean success = false;

        try{
            //create payment order
            PaymentRequest paymentRequest = new PaymentRequest(
                    request.registrationId(),
                    request.eventId(),
                    "paymentEventInPaymentService",
                    0L,
                    request.amount(),
                    request.currency()
            );

            PaymentOrderResponse paymentOrderResponse = paymentService.createOrder(paymentRequest);

            System.out.println("handlePaymentOrderRequest - "+request.registrationId());

            System.out.println("PaymentOrderRequestConsumer PaymentOrderResponse - " + paymentOrderResponse);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                    RabbitMQConfig.PAYMENT_ORDER_RESPONSE_ROUTING_KEY,
                    paymentOrderResponse
            );
        }
        catch (Exception e){
            e.printStackTrace();
        }

//        PaymentOrderResponse paymentOrderResponse = new PaymentOrderResponse(
//                request.registrationId(),
//                request.eventId(),
//
//                request.amount(),
//                request.currency(),
//                "Update this based on DB or other ops status"
//        );

    }
}
