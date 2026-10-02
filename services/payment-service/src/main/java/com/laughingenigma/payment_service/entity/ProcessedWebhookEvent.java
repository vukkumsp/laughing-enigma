package com.laughingenigma.payment_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "processed_webhook_events",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_webhook_event_id",
                columnNames = "razorpay_event_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class ProcessedWebhookEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String razorpayEventId;

    @Column(nullable = false)
    private Instant processedAt;
}
