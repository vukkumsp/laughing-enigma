package com.laughingenigma.payment_service.config;

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

    // Incoming command
    public static final String SAGA_COMMAND_EXCHANGE =
            "saga.command.exchange";
    public static final String PAYMENT_ORDER_REQUEST_QUEUE =
            "payment-order-request-queue";
    public static final String PAYMENT_ORDER_REQUEST_ROUTING_KEY =
            "payment.order.request";
    public static final String PAYMENT_VERIFY_REQUEST_QUEUE =
            "payment-verify-request-queue";
    public static final String PAYMENT_VERIFY_REQUEST_ROUTING_KEY =
            "payment.verify.request";

    // Outgoing response
    public static final String SAGA_RESPONSE_EXCHANGE =
            "saga.response.exchange";
    public static final String PAYMENT_ORDER_RESPONSE_ROUTING_KEY =
            "payment.order.response";
    public static final String PAYMENT_VERIFY_RESPONSE_ROUTING_KEY =
            "payment.verify.response";

    @Bean
    public TopicExchange sagaCommandExchange() {
        return new TopicExchange(SAGA_COMMAND_EXCHANGE);
    }

    @Bean
    public Queue paymentOrderRequestQueue() {
        return new Queue(PAYMENT_ORDER_REQUEST_QUEUE);
    }
    @Bean
    public Queue paymentVerifyRequestQueue() {
        return new Queue(PAYMENT_VERIFY_REQUEST_QUEUE);
    }

    @Bean
    public Binding paymentOrderRequestBinding(
            Queue paymentOrderRequestQueue,
            TopicExchange sagaCommandExchange) {
        return BindingBuilder
                .bind(paymentOrderRequestQueue)
                .to(sagaCommandExchange)
                .with(PAYMENT_ORDER_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding paymentVerifyRequestBinding(
            Queue paymentVerifyRequestQueue,
            TopicExchange sagaCommandExchange) {
        return BindingBuilder
                .bind(paymentVerifyRequestQueue)
                .to(sagaCommandExchange)
                .with(PAYMENT_VERIFY_REQUEST_ROUTING_KEY);
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
