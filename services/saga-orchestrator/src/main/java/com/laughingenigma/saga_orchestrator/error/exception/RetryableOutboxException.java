package com.laughingenigma.saga_orchestrator.error.exception;

public class RetryableOutboxException extends RuntimeException {

    public RetryableOutboxException(String message, Throwable cause) {
        super(message, cause);
    }
}
