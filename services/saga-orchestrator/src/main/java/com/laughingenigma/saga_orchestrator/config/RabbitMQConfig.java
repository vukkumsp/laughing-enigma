package com.laughingenigma.saga_orchestrator.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Incoming responses
    public static final String SAGA_RESPONSE_EXCHANGE =
            "saga.response.exchange";
    public static final String CUSTOMER_VALIDATION_RESPONSE_QUEUE =
            "customer-validation-response-queue";
    public static final String CUSTOMER_VALIDATION_RESPONSE_ROUTING_KEY =
            "customer.validation.response";
    public static final String SEAT_RESERVATION_RESPONSE_QUEUE =
            "seat-reservation-response-queue";
    public static final String SEAT_RESERVATION_RESPONSE_ROUTING_KEY =
            "seat.reservation.response";
    public static final String PAYMENT_ORDER_RESPONSE_QUEUE =
            "payment-order-response-queue";
    public static final String PAYMENT_ORDER_RESPONSE_ROUTING_KEY =
            "payment.order.response";
    public static final String PAYMENT_VERIFY_RESPONSE_QUEUE =
            "payment-verify-response-queue";
    public static final String PAYMENT_VERIFY_RESPONSE_ROUTING_KEY =
            "payment.verify.response";

    //Outgoing commands
    public static final String SAGA_COMMAND_EXCHANGE =
            "saga.command.exchange";
    public static final String CUSTOMER_VALIDATION_REQUEST_QUEUE =
            "customer-validation-request-queue";
    public static final String CUSTOMER_VALIDATION_REQUEST_ROUTING_KEY =
            "customer.validation.request";
    public static final String SEAT_RESERVATION_REQUEST_QUEUE =
            "seat-reservation-request-queue";
    public static final String SEAT_RESERVATION_REQUEST_ROUTING_KEY =
            "seat.reservation.request";
    public static final String PAYMENT_ORDER_REQUEST_QUEUE =
            "payment-order-request-queue";
    public static final String PAYMENT_ORDER_REQUEST_ROUTING_KEY =
            "payment.order.request";
    public static final String PAYMENT_VERIFY_REQUEST_QUEUE =
            "payment-verify-request-queue";
    public static final String PAYMENT_VERIFY_REQUEST_ROUTING_KEY =
            "payment.verify.request";


    @Bean
    public TopicExchange sagaResponseExchange() {
        return new TopicExchange(SAGA_RESPONSE_EXCHANGE);
    }
    @Bean
    public Queue customerValidationResponseQueue() {
        return new Queue(CUSTOMER_VALIDATION_RESPONSE_QUEUE);
    }
    @Bean
    public Queue seatReservationResponseQueue() {
        return new Queue(SEAT_RESERVATION_RESPONSE_QUEUE);
    }
    @Bean
    public Queue paymentOrderResponseQueue() {
        return new Queue(PAYMENT_ORDER_RESPONSE_QUEUE);
    }
    @Bean
    public Queue paymentVerifyResponseQueue() {
        return new Queue(PAYMENT_VERIFY_RESPONSE_QUEUE);
    }


    @Bean
    public Binding customerValidationResponseBinding(
            Queue customerValidationResponseQueue,
            TopicExchange sagaResponseExchange) {
        return BindingBuilder
                .bind(customerValidationResponseQueue)
                .to(sagaResponseExchange)
                .with(CUSTOMER_VALIDATION_RESPONSE_ROUTING_KEY);
    }
    @Bean
    public Binding seatReservationResponseBinding(
            Queue seatReservationResponseQueue,
            TopicExchange sagaResponseExchange) {
        return BindingBuilder
                .bind(seatReservationResponseQueue)
                .to(sagaResponseExchange)
                .with(SEAT_RESERVATION_RESPONSE_ROUTING_KEY);
    }
    @Bean
    public Binding paymentOrderResponseBinding(
            Queue paymentOrderResponseQueue,
            TopicExchange sagaResponseExchange) {
        return BindingBuilder
                .bind(paymentOrderResponseQueue)
                .to(sagaResponseExchange)
                .with(PAYMENT_ORDER_RESPONSE_ROUTING_KEY);
    }
    @Bean
    public Binding paymentVerifyResponseBinding(
            Queue paymentVerifyResponseQueue,
            TopicExchange sagaResponseExchange) {
        return BindingBuilder
                .bind(paymentVerifyResponseQueue)
                .to(sagaResponseExchange)
                .with(PAYMENT_VERIFY_RESPONSE_ROUTING_KEY);
    }

    /* Common */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        RabbitTemplate template =
                new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
