package com.laughingenigma.payment_service.consumer;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.*;
import com.laughingenigma.payment_service.publisher.PaymentOrderResponsePublisher;
import com.laughingenigma.payment_service.service.PaymentService;
import com.razorpay.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentOrderRequestConsumer {

    private final PaymentService paymentService;
    private final PaymentOrderResponsePublisher publisher;

    public PaymentOrderRequestConsumer(
            PaymentService paymentService,
            PaymentOrderResponsePublisher publisher) {
        this.paymentService = paymentService;
        this.publisher = publisher;
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
                    request.customerId(),
                    request.amount(),
                    request.currency()
            );

            PaymentOrderResponse paymentOrderResponse = paymentService.createOrder(paymentRequest);

            System.out.println("handlePaymentOrderRequest - "+request.registrationId());

            publisher.publish(paymentOrderResponse);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
