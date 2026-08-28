package com.laughingenigma.payment_service.consumer;

import com.laughingenigma.payment_service.config.RabbitMQConfig;
import com.laughingenigma.payment_service.dto.PaymentRequest;
import com.laughingenigma.payment_service.dto.PaymentResponse;
import com.laughingenigma.payment_service.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestConsumer {

    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;

    public PaymentRequestConsumer(
            PaymentService paymentService,
            RabbitTemplate rabbitTemplate) {
        this.paymentService = paymentService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_REQUEST_QUEUE
    )
    public void handlePaymentRequest(
            PaymentRequest request) {

        boolean success = false;

        try{
            //trigger payment flow
            //success = paymentService.completePayment(request);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        PaymentResponse seatReservationResponse = new PaymentResponse(
                request.registrationId(),
                request.eventId(),
                success
        );
        System.out.println("handlePaymentRequest - "+request.registrationId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_RESPONSE_EXCHANGE,
                RabbitMQConfig.PAYMENT_RESPONSE_ROUTING_KEY,
                seatReservationResponse
        );
    }
}
