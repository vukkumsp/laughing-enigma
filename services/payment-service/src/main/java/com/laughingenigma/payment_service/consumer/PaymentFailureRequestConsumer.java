package com.laughingenigma.payment_service.consumer;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.PaymentOrderRequest;
import com.laughingenigma.payment_service.dto.PaymentOrderResponse;
import com.laughingenigma.payment_service.dto.PaymentRequest;
import com.laughingenigma.payment_service.publisher.PaymentFailureResponsePublisher;
import com.laughingenigma.payment_service.publisher.PaymentOrderResponsePublisher;
import com.laughingenigma.payment_service.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailureRequestConsumer {

    private final PaymentService paymentService;
    private final PaymentFailureResponsePublisher publisher;

    public PaymentFailureRequestConsumer(
            PaymentService paymentService,
            PaymentFailureResponsePublisher publisher) {
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
