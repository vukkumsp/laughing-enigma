package com.laughingenigma.customer_service.config;

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
    public static final String CUSTOMER_VALIDATION_REQUEST_QUEUE =
            "customer-validation-request-queue";
    public static final String CUSTOMER_VALIDATION_REQUEST_ROUTING_KEY =
            "customer.validation.request";
    public static final String CUSTOMER_BOOKING_STATUS_UPDATE_REQUEST_QUEUE =
            "customer-booking-status-update-request-queue";
    public static final String CUSTOMER_BOOKING_STATUS_UPDATE_REQUEST_ROUTING_KEY =
            "customer.booking.status.update.request";


    // Outgoing response
    public static final String SAGA_RESPONSE_EXCHANGE =
            "saga.response.exchange";
    public static final String CUSTOMER_VALIDATION_RESPONSE_ROUTING_KEY =
            "customer.validation.response";
    public static final String CUSTOMER_BOOKING_STATUS_UPDATE_RESPONSE_ROUTING_KEY =
            "customer.booking.status.update.response";

    @Bean
    public TopicExchange sagaCommandExchange() {
        return new TopicExchange(SAGA_COMMAND_EXCHANGE);
    }

    @Bean
    public Queue customerValidationRequestQueue() {
        return new Queue(CUSTOMER_VALIDATION_REQUEST_QUEUE);
    }

    @Bean
    public Queue customerBookingStatusUpdateRequestQueue() {
        return new Queue(CUSTOMER_BOOKING_STATUS_UPDATE_REQUEST_QUEUE);
    }

    @Bean
    public Binding customerValidationRequestBinding(
            Queue customerValidationRequestQueue,
            TopicExchange sagaCommandExchange) {

        return BindingBuilder
                .bind(customerValidationRequestQueue)
                .to(sagaCommandExchange)
                .with(CUSTOMER_VALIDATION_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding customerBookingStatusUpdateRequestBinding(
            Queue customerBookingStatusUpdateRequestQueue,
            TopicExchange sagaCommandExchange) {

        return BindingBuilder
                .bind(customerBookingStatusUpdateRequestQueue)
                .to(sagaCommandExchange)
                .with(CUSTOMER_BOOKING_STATUS_UPDATE_REQUEST_ROUTING_KEY);
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
