package com.laughingenigma.notification_service.controller;

import com.laughingenigma.notification_service.email.EmailSender;
import com.laughingenigma.notification_service.kafka.event.EventDetails;
import com.laughingenigma.notification_service.kafka.event.RegistrationCompletedPayload;
import com.laughingenigma.notification_service.kafka.event.RegistrationFailedPayload;
import com.laughingenigma.notification_service.service.EmailNotificationService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/test/email")
@Profile("dev")
public class EmailTestController {
    private final EmailNotificationService emailNotificationService;

    public EmailTestController(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @PostMapping("/{statusId}")
    public ResponseEntity<Void> sendTestEmail(@PathVariable int statusId) {

        switch (statusId) {
            case 1:
                emailNotificationService.sendRegistrationCompleted(
                        new RegistrationCompletedPayload(
                                "testRegistrationId",
                                "postmanUser",
                                "test@example.com",
                                new EventDetails(
                                        1L,
                                        "eventName",
                                        Instant.now(),
                                        Instant.now()
                                )
                        )
                );
                break;
            default:
                emailNotificationService.sendRegistrationFailed(
                        new RegistrationFailedPayload(
                                "testRegistrationId",
                                "postmanUser",
                                "test@example.com",
                                new EventDetails(
                                        2L,
                                        "eventName",
                                        Instant.now(),
                                        Instant.now()
                                ),
                                "Test Failure"
                        )
                );
                break;
        }

        return ResponseEntity.accepted().build();
    }
}
