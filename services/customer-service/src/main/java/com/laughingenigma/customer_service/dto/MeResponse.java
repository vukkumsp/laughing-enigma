package com.laughingenigma.customer_service.dto;

public class MeResponse {
    private String message;

    public MeResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}