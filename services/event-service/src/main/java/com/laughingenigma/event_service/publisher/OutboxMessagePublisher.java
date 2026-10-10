package com.laughingenigma.event_service.publisher;

import com.laughingenigma.event_service.dto.*;
import com.laughingenigma.event_service.entity.OutboxMessage;
import com.laughingenigma.event_service.entity.OutboxMessageType;
import com.laughingenigma.event_service.entity.OutboxStatus;
import com.laughingenigma.event_service.error.exception.NonRetryableOutboxException;
import com.laughingenigma.event_service.repository.OutboxMessageRepository;
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
    private final SeatReservationResponsePublisher  seatReservationResponsePublisher;
    private final SeatUnreserveResponsePublisher SeatUnreserveResponsePublisher;

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
            SeatReservationResponsePublisher seatReservationResponsePublisher,
            SeatUnreserveResponsePublisher SeatUnreserveResponsePublisher) {

        this.outboxMessageRepository = outboxMessageRepository;
        this.objectMapper = objectMapper;

        this.seatReservationResponsePublisher = seatReservationResponsePublisher;
        this.SeatUnreserveResponsePublisher = SeatUnreserveResponsePublisher;
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
                case OutboxMessageType.SEAT_RESERVATION_RESPONSE:
                    SeatReservationResponse seatReservationResponse = deserialize(message, SeatReservationResponse.class);
                    System.out.println("publishing response with registrationId " + seatReservationResponse.registrationId());
                    seatReservationResponsePublisher.publish(seatReservationResponse);
                    break;
                case OutboxMessageType.SEAT_UNRESERVE_RESPONSE:
                    SeatUnreserveResponse seatUnreserveResponse = deserialize(message, SeatUnreserveResponse.class);
                    System.out.println("publishing response with registrationId " + seatUnreserveResponse.registrationId());
                    SeatUnreserveResponsePublisher.publish(seatUnreserveResponse);
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
