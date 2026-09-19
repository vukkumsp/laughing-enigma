package com.laughingenigma.payment_service.dto;

import java.time.LocalDateTime;

public record PaymentVerifyResponse (
        String registrationId,
//---------------------------------------
        Long customerId,
//---------------------------------------
        String orderId,
        String paymentId,
        String status
){
}
