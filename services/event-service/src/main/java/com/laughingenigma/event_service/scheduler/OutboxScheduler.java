package com.laughingenigma.event_service.scheduler;

import com.laughingenigma.event_service.publisher.OutboxMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxMessagePublisher publisher;

    private volatile boolean applicationReady = false;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        applicationReady = true;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay}")
    public void publishPendingMessages() {

        if (!applicationReady) {
            System.out.println("Application not ready to publish messages");
            return;
        }

        publisher.publishPendingMessages();
    }
}