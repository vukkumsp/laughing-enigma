package com.laughingenigma.notification_service.email;

public interface EmailSender {

    void send(
        String recipient,
        String subject,
        String body
    );
}
