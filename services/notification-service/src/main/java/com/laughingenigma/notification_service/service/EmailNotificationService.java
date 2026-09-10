package com.laughingenigma.notification_service.service;

import com.laughingenigma.notification_service.email.EmailSender;
import com.laughingenigma.notification_service.kafka.event.RegistrationCompletedPayload;
import com.laughingenigma.notification_service.kafka.event.RegistrationFailedPayload;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {
    EmailSender emailSender;

    public EmailNotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendRegistrationCompleted(RegistrationCompletedPayload payload) {
        String subject = "Registration Confirmed";

        String body = """
                Your registration has been confirmed.

                Event: %s
                Start: %s
                End: %s
                """.formatted(
                        payload.event().name(),
                        payload.event().startTime(),
                        payload.event().endTime()
                );

        emailSender.send(
                payload.email(),
                subject,
                body
        );
    }

    public void sendRegistrationFailed(RegistrationFailedPayload payload) {
        String subject = "Registration Failed";

        String body = """
                Your registration has been failed.
        
                Event: %s
                Reason: %s
                """.formatted(
                payload.event().name(),
                payload.reason()
        );

        emailSender.send(
                payload.email(),
                subject,
                body
        );
    }
}
