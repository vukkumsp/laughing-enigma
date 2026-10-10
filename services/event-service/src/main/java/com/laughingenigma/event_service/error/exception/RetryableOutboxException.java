package com.laughingenigma.event_service.error.exception;

public class RetryableOutboxException extends RuntimeException {

    public RetryableOutboxException(String message, Throwable cause) {
        super(message, cause);
    }
}
