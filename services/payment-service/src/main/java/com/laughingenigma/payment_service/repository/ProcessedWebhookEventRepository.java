package com.laughingenigma.payment_service.repository;

import com.laughingenigma.payment_service.entity.ProcessedWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedWebhookEventRepository
        extends JpaRepository<ProcessedWebhookEvent, Long> {
    boolean existsByRazorpayEventId(String eventId);
}
