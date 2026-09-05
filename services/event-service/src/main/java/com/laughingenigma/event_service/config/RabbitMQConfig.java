package com.laughingenigma.event_service.config;

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
    public static final String SEAT_RESERVATION_REQUEST_QUEUE =
            "seat-reservation-request-queue";
    public static final String SEAT_RESERVATION_REQUEST_ROUTING_KEY =
            "seat.reservation.request";
    public static final String SEAT_UNRESERVE_REQUEST_QUEUE =
            "seat-unreserve-request-queue";
    public static final String SEAT_UNRESERVE_REQUEST_ROUTING_KEY =
            "seat.unreserve.request";

    // Outgoing response
    public static final String SAGA_RESPONSE_EXCHANGE =
            "saga.response.exchange";
    public static final String SEAT_RESERVATION_RESPONSE_ROUTING_KEY =
            "seat.reservation.response";

    @Bean
    public TopicExchange sagaCommandExchange() {
        return new TopicExchange(SAGA_COMMAND_EXCHANGE);
    }

    @Bean
    public Queue seatReservationRequestQueue() {
        return new Queue(SEAT_RESERVATION_REQUEST_QUEUE);
    }
    @Bean
    public Queue seatUnreserveRequestQueue() {
        return new Queue(SEAT_UNRESERVE_REQUEST_QUEUE);
    }


    @Bean
    public Binding seatReservationRequestBinding(
            Queue seatReservationRequestQueue,
            TopicExchange sagaCommandExchange) {

        return BindingBuilder
                .bind(seatReservationRequestQueue)
                .to(sagaCommandExchange)
                .with(SEAT_RESERVATION_REQUEST_ROUTING_KEY);
    }
    @Bean
    public Binding seatUnreserveRequestBinding(
            Queue seatUnreserveRequestQueue,
            TopicExchange sagaCommandExchange) {

        return BindingBuilder
                .bind(seatUnreserveRequestQueue)
                .to(sagaCommandExchange)
                .with(SEAT_UNRESERVE_REQUEST_ROUTING_KEY);
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
