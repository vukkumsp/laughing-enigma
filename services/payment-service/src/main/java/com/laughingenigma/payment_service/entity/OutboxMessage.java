package com.laughingenigma.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "outbox_messages",
        indexes = {
                @Index(
                        name = "idx_outbox_status_created_at",
                        columnList = "status, created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID outboxMessageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private OutboxMessageType messageType;

    @Column(nullable = false, length = 200)
    private String exchange;

    @Column(nullable = false, length = 200)
    private String routingKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant publishedAt;

    // retryCount represents failed attempts, not the current attempt number.
    @Column(nullable = false)
    private int retryCount;

    private Instant nextRetryAt;

    private String failureType;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    private Instant failedAt;
}
