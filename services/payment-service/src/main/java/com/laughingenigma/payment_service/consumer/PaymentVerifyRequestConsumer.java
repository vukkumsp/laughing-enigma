//package com.laughingenigma.payment_service.consumer;
//
//import com.laughingenigma.payment_service.config.RabbitMQConfig;
//import com.laughingenigma.payment_service.dto.*;
//import com.laughingenigma.payment_service.dto.event.PaymentFailureResponse;
//import com.laughingenigma.payment_service.publisher.PaymentFailureResponsePublisher;
//import com.laughingenigma.payment_service.publisher.PaymentVerifyResponsePublisher;
//import com.laughingenigma.payment_service.service.PaymentService;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PaymentVerifyRequestConsumer {
//
//    private final PaymentService paymentService;
//    private final PaymentVerifyResponsePublisher publisher;
//    private final PaymentFailureResponsePublisher failurePublisher;
//
//    public PaymentVerifyRequestConsumer(
//            PaymentService paymentService,
//            PaymentVerifyResponsePublisher publisher,
//            PaymentFailureResponsePublisher failurePublisher) {
//        this.paymentService = paymentService;
//        this.publisher = publisher;
//        this.failurePublisher = failurePublisher;
//    }
//
//    @RabbitListener(
//            queues = RabbitMQConfig.PAYMENT_VERIFY_REQUEST_QUEUE
//    )
//    public void handlePaymentVerifyRequest(PaymentVerifyRequest request) {
//        System.out.println("PaymentVerifyRequestConsumer PaymentVerifyRequest - " + request);
//
//        try{
//            PaymentVerificationRequest paymentVerificationRequest = new PaymentVerificationRequest(
//                    request.razorpayOrderId(), request.razorpayPaymentId(), request.razorpaySignature());
//            // Verify payment signature and other validations
//            PaymentVerificationResponse response = paymentService.verifyPayment(paymentVerificationRequest);
//
//            PaymentVerifyResponse paymentVerifyResponse = new PaymentVerifyResponse(
//                request.registrationId(), request.eventId(),
//                request.customerId(), request.username(), request.email(), request.firstName(), request.lastName(),
//                request.eventName(), request.eventDate(),
//                response.orderId(), response.paymentId(), response.status()
//            );
//
//            System.out.println("handlePaymentVerifyRequest - "+request.registrationId());
//
//            publisher.publish(paymentVerifyResponse);
//        }
//        catch (Exception e){
//            //payment failed start compensation
//            PaymentFailureResponse paymentFailureResponse = new PaymentFailureResponse(
//                    request.registrationId(),
//                    request.eventId()
//            );
//            failurePublisher.publish(paymentFailureResponse);
//
//            e.printStackTrace();
//        }
//
//    }
//}
