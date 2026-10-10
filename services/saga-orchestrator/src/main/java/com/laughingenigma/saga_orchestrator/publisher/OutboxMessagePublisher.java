package com.laughingenigma.saga_orchestrator.publisher;

import com.laughingenigma.saga_orchestrator.dto.*;
import com.laughingenigma.saga_orchestrator.entity.OutboxMessage;
import com.laughingenigma.saga_orchestrator.entity.OutboxMessageType;
import com.laughingenigma.saga_orchestrator.entity.OutboxStatus;
import com.laughingenigma.saga_orchestrator.error.exception.NonRetryableOutboxException;
import com.laughingenigma.saga_orchestrator.kafka.event.RegistrationEvent;
import com.laughingenigma.saga_orchestrator.kafka.producer.KafkaRegistrationEventProducer;
import com.laughingenigma.saga_orchestrator.repository.OutboxMessageRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class OutboxMessagePublisher {

    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectMapper objectMapper;

    //rabbitmq publishers
    private final CustomerValidationRequestPublisher customerValidationRequestPublisher;
    private final SeatReservationRequestPublisher  seatReservationRequestPublisher;
    private final PaymentOrderRequestPublisher paymentOrderRequestPublisher;
    private final SeatUnreserveRequestPublisher seatUnreserveRequestPublisher;

    //kafka publishers
    private final KafkaRegistrationEventProducer kafkaRegistrationEventProducer;

    /*
        So, scheduler only starts after Application is fully ready
        with tables created if not already done.
     */
    private volatile boolean applicationReady = false;
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        applicationReady = true;
    }

    private static final int MAX_ATTEMPTS = 4;

    public OutboxMessagePublisher(
            OutboxMessageRepository outboxMessageRepository,
            ObjectMapper objectMapper,

            CustomerValidationRequestPublisher customerValidationRequestPublisher,
            SeatReservationRequestPublisher seatReservationRequestPublisher,
            PaymentOrderRequestPublisher paymentOrderRequestPublisher,
            SeatUnreserveRequestPublisher seatUnreserveRequestPublisher,

            KafkaRegistrationEventProducer kafkaRegistrationEventProducer) {

        this.outboxMessageRepository = outboxMessageRepository;
        this.objectMapper = objectMapper;

        this.customerValidationRequestPublisher = customerValidationRequestPublisher;
        this.seatReservationRequestPublisher = seatReservationRequestPublisher;
        this.paymentOrderRequestPublisher = paymentOrderRequestPublisher;
        this.seatUnreserveRequestPublisher = seatUnreserveRequestPublisher;

        this.kafkaRegistrationEventProducer = kafkaRegistrationEventProducer;
    }

    @Transactional
    public void publishPendingMessages() {

        //System.out.println("STARTED publishPendingMessages");

        List<OutboxMessage> messages =
                outboxMessageRepository.findReadyMessages(
                        OutboxStatus.PENDING,
                        Instant.now(),
                        PageRequest.of(0, 100)
                );

        for (OutboxMessage message : messages) {
            publish(message);
        }
    }

    private <T> T deserialize(
            OutboxMessage message, Class<T> clazz) {

        try {

            return objectMapper.readValue(
                    message.getPayload(),
                    clazz
            );

        } catch (JacksonException e) {
            throw new NonRetryableOutboxException(
                    "Invalid Outbox payload. " +
                            "outboxMessageId=" +
                            message.getOutboxMessageId(),
                    e
            );
        }
    }

    public void publish(OutboxMessage message) {

        //System.out.println("STARTED publish message: " + message);

        try {
            switch(message.getMessageType()) {
                case OutboxMessageType.CUSTOMER_VALIDATION_REQUEST:
                    CustomerValidationRequest customerValidationRequest
                            = deserialize(message, CustomerValidationRequest.class);
                    System.out.println("publishing response with registrationId " + customerValidationRequest.registrationId());
                    customerValidationRequestPublisher.publish(customerValidationRequest);
                    break;
                case OutboxMessageType.SEAT_RESERVATION_REQUEST:
                    SeatReservationRequest seatReservationRequest = deserialize(message, SeatReservationRequest.class);
                    System.out.println("publishing response with registrationId " + seatReservationRequest.registrationId());
                    seatReservationRequestPublisher.publish(seatReservationRequest);
                    break;
                case OutboxMessageType.PAYMENT_ORDER_REQUEST:
                    PaymentOrderRequest paymentOrderRequest = deserialize(message, PaymentOrderRequest.class);
                    System.out.println("publishing response with registrationId " + paymentOrderRequest.registrationId());
                    paymentOrderRequestPublisher.publish(paymentOrderRequest);
                    break;
                case OutboxMessageType.SEAT_UNRESERVE_REQUEST:
                    SeatUnreserveRequest seatUnreserveRequest = deserialize(message, SeatUnreserveRequest.class);
                    System.out.println("publishing response with registrationId " + seatUnreserveRequest.registrationId());
                    seatUnreserveRequestPublisher.publish(seatUnreserveRequest);
                    break;
                case OutboxMessageType.REGISTRATION_COMPLETED:
                case OutboxMessageType.REGISTRATION_FAILED:
                    RegistrationEvent registrationEvent = deserialize(message, RegistrationEvent.class);
                    kafkaRegistrationEventProducer.publish(registrationEvent);
                    break;
                default:
                    System.out.println(
                            "OUTBOX: Unknown message type: " +
                                    message.getMessageType()
                    );

                    throw new NonRetryableOutboxException(
                            "Unknown Outbox message type: " +
                                    message.getMessageType()
                    );
            }

            System.out.println("published response with payload " + message.getPayload());

            message.setStatus(OutboxStatus.PUBLISHED);
            message.setPublishedAt(Instant.now());

            outboxMessageRepository.save(message);

            System.out.println(
                    "OUTBOX: Message published successfully. " +
                            "outboxMessageId=" +
                            message.getOutboxMessageId()
            );
        } catch (NonRetryableOutboxException e) {
            handleNonRetryableFailure(message, e);

        } catch (Exception e) {
            System.out.println(
                    "OUTBOX: Failed to publish message. " +
                            "outboxMessageId=" +
                            message.getOutboxMessageId()
            );
            handleRetryableFailure(message, e);
        }
    }

    private void handleRetryableFailure(
            OutboxMessage message,
            Exception exception) {

        int retryCount = message.getRetryCount() + 1;

        message.setRetryCount(retryCount);

        if (retryCount < MAX_ATTEMPTS) {

            Duration backoff = calculateBackoff(retryCount);

            message.setNextRetryAt(
                    Instant.now().plus(backoff)
            );

            outboxMessageRepository.save(message);

            System.out.println(
                    "OUTBOX: Publish failed. " +
                            "Retry " + retryCount +
                            "/" + MAX_ATTEMPTS +
                            " scheduled in " + backoff +
                            ". " +
                            "outboxMessageId=" +
                            message.getOutboxMessageId()
            );

        } else {

            // DLQ will handle this in Phase 3.
            message.setStatus(OutboxStatus.FAILED);
            outboxMessageRepository.save(message);

            System.out.println(
                    "OUTBOX: Maximum retry attempts reached. " +
                            "outboxMessageId=" +
                            message.getOutboxMessageId()
            );
        }
    }

    private void handleNonRetryableFailure(
            OutboxMessage message,
            Exception exception) {

        System.out.println(
                "OUTBOX: Non-retryable failure. " +
                        "outboxMessageId=" +
                        message.getOutboxMessageId() +
                        ", reason=" +
                        exception.getMessage()
        );

        // Phase 3 will introduce the DLQ state/handling.
        message.setStatus(OutboxStatus.FAILED);
        outboxMessageRepository.save(message);
    }

    // retryCount represents failed attempts, not the current attempt number.
    private Duration calculateBackoff(int retryCount) {
        return Duration.ofSeconds(
                (long) Math.pow(2, retryCount - 1)
        );
    }
}
