package com.laughingenigma.payment_service.publisher;

import com.laughingenigma.payment_service.dto.PaymentVerifyRequest;
import com.laughingenigma.payment_service.dto.PaymentVerifyResponse;
import com.laughingenigma.payment_service.entity.OutboxMessage;
import com.laughingenigma.payment_service.entity.OutboxMessageType;
import com.laughingenigma.payment_service.entity.OutboxStatus;
import com.laughingenigma.payment_service.error.exception.NonRetryableOutboxException;
import com.laughingenigma.payment_service.repository.OutboxMessageRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class OutboxMessagePublisher {

    private final OutboxMessageRepository outboxMessageRepository;
    private final PaymentVerifyResponsePublisher paymentVerifyResponsePublisher;
    private final ObjectMapper objectMapper;

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
            PaymentVerifyResponsePublisher paymentVerifyResponsePublisher,
            ObjectMapper objectMapper) {

        this.outboxMessageRepository = outboxMessageRepository;
        this.paymentVerifyResponsePublisher =
                paymentVerifyResponsePublisher;
        this.objectMapper = objectMapper;
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

    private PaymentVerifyResponse deserialize(
            OutboxMessage message) {

        try {

            return objectMapper.readValue(
                    message.getPayload(),
                    PaymentVerifyResponse.class
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
            if (OutboxMessageType.PAYMENT_VERIFY_RESPONSE
                    .equals(message.getMessageType())) {

                PaymentVerifyResponse response = deserialize(message);

                System.out.println("publishing response with registrationId " + response.registrationId());
                paymentVerifyResponsePublisher.publish(response);

            } else {
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

        // Keep the message PENDING for now.
        //
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
