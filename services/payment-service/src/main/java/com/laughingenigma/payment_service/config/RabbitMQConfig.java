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
    public static final String PAYMENT_REQUEST_QUEUE =
            "payment-request-queue";
    public static final String PAYMENT_REQUEST_ROUTING_KEY =
            "payment.request";

    // Outgoing response
    public static final String SAGA_RESPONSE_EXCHANGE =
            "saga.response.exchange";
    public static final String PAYMENT_RESPONSE_ROUTING_KEY =
            "payment.response";

    @Bean
    public TopicExchange sagaCommandExchange() {
        return new TopicExchange(SAGA_COMMAND_EXCHANGE);
    }

    @Bean
    public Queue paymentRequestQueue() {
        return new Queue(PAYMENT_REQUEST_QUEUE);
    }

    @Bean
    public Binding paymentRequestBinding(
            Queue paymentRequestQueue,
            TopicExchange sagaCommandExchange) {

        return BindingBuilder
                .bind(paymentRequestQueue)
                .to(sagaCommandExchange)
                .with(PAYMENT_REQUEST_ROUTING_KEY);
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
