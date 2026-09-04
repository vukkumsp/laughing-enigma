package com.laughingenigma.payment_service.consumer;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.*;
import com.laughingenigma.payment_service.publisher.PaymentVerifyResponsePublisher;
import com.laughingenigma.payment_service.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentVerifyRequestConsumer {

    private final PaymentService paymentService;
    private final PaymentVerifyResponsePublisher publisher;

    public PaymentVerifyRequestConsumer(
            PaymentService paymentService,
            PaymentVerifyResponsePublisher publisher) {
        this.paymentService = paymentService;
        this.publisher = publisher;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_VERIFY_REQUEST_QUEUE
    )
    public void handlePaymentVerifyRequest(
            PaymentVerifyRequest request) {
        System.out.println("PaymentVerifyRequestConsumer PaymentVerifyRequest - " + request);

        try{
            PaymentVerificationRequest paymentVerificationRequest = new PaymentVerificationRequest(
                    request.razorpayOrderId(), request.razorpayPaymentId(), request.razorpaySignature());
            // Verify payment signature and other validations
            PaymentVerificationResponse response = paymentService.verifyPayment(paymentVerificationRequest);

            PaymentVerifyResponse paymentVerifyResponse = new PaymentVerifyResponse(
                request.registrationId(), request.eventId(),
                response.orderId(), response.paymentId(), response.status()
            );

            System.out.println("handlePaymentVerifyRequest - "+request.registrationId());

            publisher.publish(paymentVerifyResponse);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
