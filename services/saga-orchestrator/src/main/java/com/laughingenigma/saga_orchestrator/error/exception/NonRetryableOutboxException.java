package com.laughingenigma.saga_orchestrator.error.exception;

public class NonRetryableOutboxException extends RuntimeException {

    public NonRetryableOutboxException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonRetryableOutboxException(String message) {
        super(message);
    }
}
