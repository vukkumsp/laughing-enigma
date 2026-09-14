package com.laughingenigma.saga_orchestrator.saga.registration_saga;

import com.laughingenigma.saga_orchestrator.dto.*;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.dto.RegistrationContextDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class DtoFactory {
    RegistrationContextDto getRegistrationContextDto(SagaStep sagaStep, Map<String, Object> context) {
        switch (sagaStep) {
            case REGISTRATION_STARTED:
                return new RegistrationRequest(
                        (String) context.get("registrationId"),
                        (Long) context.get("eventId")
                );

            case CUSTOMER_VALIDATION:
                return new CustomerValidationRequest(
                        (String) context.get("registrationId"),
                        (Long) context.get("eventId"),
                        (String) context.get("username")
                );
            case CUSTOMER_VALIDATED:
            case CUSTOMER_VALIDATION_FAILED:
                return new CustomerValidationResponse(
                        (String) context.get("registrationId"),
                        (Long) context.get("eventId"),
                        (boolean) context.get("valid"),
                        (Long) context.get("customerId"),
                        (String) context.get("username"),
                        (String) context.get("email"),
                        (String) context.get("firstName"),
                        (String) context.get("lastName")
                );
            case SEAT_RESERVATION:
            case SEAT_RESERVED:
                return new SeatReservationRequest(
                        (String) context.get("registrationId"),
                        (Long) context.get("eventId"),
                        (Long) context.get("customerId"),
                        (String) context.get("username"),
                        (String) context.get("email"),
                        (String) context.get("firstName"),
                        (String) context.get("lastName")
                );
            case SEAT_RESERVATION_FAILED:
                return new SeatReservationResponse(
                        (String) context.get("registrationId"),
                        (Long) context.get("eventId"),
                        (Long) context.get("customerId"),
                        (String) context.get("username"),
                        (String) context.get("email"),
                        (String) context.get("firstName"),
                        (String) context.get("lastName"),
                        (String) context.get("eventName"),
                        (LocalDateTime) context.get("eventDate"),
                        (BigDecimal) context.get("price"),
                        (String) context.get("currency"),
                        (boolean) context.get("success")
                );
            case SEAT_RELEASING:
                return new SeatUnreserveRequest(
                        (String) context.get("registrationId"),
                        (Long) context.get("eventId"),
                        (Long) context.get("customerId"),
                        (String) context.get("username"),
                        (String) context.get("email"),
                        (String) context.get("firstName"),
                        (String) context.get("lastName"),
                        (String) context.get("eventName"),
                        (LocalDateTime) context.get("eventDate")
                );
            case PAYMENT_REQUIRED:
                return new PaymentOrderResponse(
                        (String) context.get("registrationId"),
                        (Long) context.get("eventId"),
                        (Long) context.get("customerId"),
                        (String) context.get("username"),
                        (String) context.get("email"),
                        (String) context.get("firstName"),
                        (String) context.get("lastName"),
                        (String) context.get("eventName"),
                        (LocalDateTime) context.get("eventDate"),
                        (BigDecimal) context.get("price"),
                        (String) context.get("currency"),
                        (String) context.get("orderId"),
                        (String) context.get("status")
                );
            case PAYMENT_VERIFICATION_STARTED:
                return null; // NOT REQUIRED
            case PAYMENT_SUCCESS:
            case PAYMENT_FAILED:
            case REGISTRATION_COMPLETED:
                return new PaymentVerifyResponse(
                        (String) context.get("registrationId"),
                        (Long) context.get("eventId"),
                        (Long) context.get("customerId"),
                        (String) context.get("username"),
                        (String) context.get("email"),
                        (String) context.get("firstName"),
                        (String) context.get("lastName"),
                        (String) context.get("eventName"),
                        (LocalDateTime) context.get("eventDate"),

                        (String) context.get("orderId"),
                        (String) context.get("paymentId"),
                        (String) context.get("status")
                );
            case REGISTRATION_COMPENSATED:
                return new SeatUnreserveResponse(
                        (String) context.get("registrationId"),
                        (Long) context.get("eventId"),
                        (boolean) context.get("success")
                );
            default:
                return null;
        }
    }
}
