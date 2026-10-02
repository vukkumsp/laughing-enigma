package com.laughingenigma.payment_service.publisher;

import com.laughingenigma.payment_service.dto.PaymentVerifyResponse;
import com.laughingenigma.payment_service.entity.OutboxMessage;
import com.laughingenigma.payment_service.entity.OutboxMessageType;
import com.laughingenigma.payment_service.entity.OutboxStatus;
import com.laughingenigma.payment_service.repository.OutboxMessageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxMessagePublisher {

    private final OutboxMessageRepository outboxMessageRepository;
    private final PaymentVerifyResponsePublisher paymentVerifyResponsePublisher;
    private final ObjectMapper objectMapper;

    public OutboxMessagePublisher(
            OutboxMessageRepository outboxMessageRepository,
            PaymentVerifyResponsePublisher paymentVerifyResponsePublisher,
            ObjectMapper objectMapper) {

        this.outboxMessageRepository = outboxMessageRepository;
        this.paymentVerifyResponsePublisher =
                paymentVerifyResponsePublisher;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingMessages() {

        System.out.println("STARTED publishPendingMessages");

        List<OutboxMessage> messages =
                outboxMessageRepository
                        .findTop100ByStatusOrderByCreatedAtAsc(
                                OutboxStatus.PENDING
                        );

        for (OutboxMessage message : messages) {

            try {


                if (OutboxMessageType.PAYMENT_VERIFY_RESPONSE
                        .equals(message.getMessageType())) {

                    PaymentVerifyResponse response =
                            objectMapper.readValue(
                                    message.getPayload(),
                                    PaymentVerifyResponse.class
                            );

                    System.out.println("publishing response with registrationId " + response.registrationId());
                    paymentVerifyResponsePublisher.publish(response);

                } else {
                    System.out.println(
                            "OUTBOX: Unknown message type: " +
                                    message.getMessageType()
                    );

                    continue;
                }

                System.out.println("published response with payload " + message.getPayload());

                message.setStatus(OutboxStatus.PUBLISHED);
                message.setPublishedAt(Instant.now());

                outboxMessageRepository.save(message);

            } catch (Exception e) {

                System.out.println(
                        "OUTBOX: Failed to publish message. " +
                                "outboxMessageId=" +
                                message.getOutboxMessageId()
                );

                e.printStackTrace();
            }
        }
    }
}
