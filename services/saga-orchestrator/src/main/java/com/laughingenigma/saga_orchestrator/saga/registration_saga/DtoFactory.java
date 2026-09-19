package com.laughingenigma.saga_orchestrator.saga.registration_saga;

import com.laughingenigma.saga_orchestrator.dto.*;
import com.laughingenigma.saga_orchestrator.entity.SagaStep;
import com.laughingenigma.saga_orchestrator.saga.registration_saga.dto.RegistrationContextDto;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class DtoFactory {

    private final ObjectMapper mapper;

    public DtoFactory(ObjectMapper mapper){
        this.mapper = mapper;
    }

    public RegistrationContextDto getRegistrationContextDto(SagaStep sagaStep, Map<String, Object> context) {
        switch (sagaStep) {
            case REGISTRATION_STARTED:
                return new RegistrationRequest(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class)
                );

            case CUSTOMER_VALIDATION:
                return new CustomerValidationRequest(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class),
                        mapper.convertValue(context.get("username"), String.class)
                );
            case CUSTOMER_VALIDATED:
            case CUSTOMER_VALIDATION_FAILED:
                return new CustomerValidationResponse(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class),
                        mapper.convertValue(context.get("valid"), Boolean.class),
                        mapper.convertValue(context.get("customerId"), Long.class),
                        mapper.convertValue(context.get("username"), String.class),
                        mapper.convertValue(context.get("email"), String.class),
                        mapper.convertValue(context.get("firstName"), String.class),
                        mapper.convertValue(context.get("lastName"), String.class)
                );
            case SEAT_RESERVATION:
            case SEAT_RESERVED:
                return new SeatReservationRequest(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class),
                        mapper.convertValue(context.get("customerId"), Long.class),
                        mapper.convertValue(context.get("username"), String.class),
                        mapper.convertValue(context.get("email"), String.class),
                        mapper.convertValue(context.get("firstName"), String.class),
                        mapper.convertValue(context.get("lastName"), String.class)
                );
            case SEAT_RESERVATION_FAILED:
                return new SeatReservationResponse(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class),
                        mapper.convertValue(context.get("customerId"), Long.class),
                        mapper.convertValue(context.get("username"), String.class),
                        mapper.convertValue(context.get("email"), String.class),
                        mapper.convertValue(context.get("firstName"), String.class),
                        mapper.convertValue(context.get("lastName"), String.class),
                        mapper.convertValue(context.get("eventName"), String.class),
                        mapper.convertValue(context.get("eventDate"), LocalDateTime.class),
                        mapper.convertValue(context.get("price"), BigDecimal.class),
                        mapper.convertValue(context.get("currency"), String.class),
                        mapper.convertValue(context.get("success"), Boolean.class)
                );
            case SEAT_RELEASING:
                return new SeatUnreserveRequest(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class),
                        mapper.convertValue(context.get("customerId"), Long.class),
                        mapper.convertValue(context.get("username"), String.class),
                        mapper.convertValue(context.get("email"), String.class),
                        mapper.convertValue(context.get("firstName"), String.class),
                        mapper.convertValue(context.get("lastName"), String.class),
                        mapper.convertValue(context.get("eventName"), String.class),
                        mapper.convertValue(context.get("eventDate"), LocalDateTime.class)
                );
            case PAYMENT_REQUIRED:
                return new PaymentOrderResponse(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class),
                        mapper.convertValue(context.get("customerId"), Long.class),
                        mapper.convertValue(context.get("username"), String.class),
                        mapper.convertValue(context.get("email"), String.class),
                        mapper.convertValue(context.get("firstName"), String.class),
                        mapper.convertValue(context.get("lastName"), String.class),
                        mapper.convertValue(context.get("eventName"), String.class),
                        mapper.convertValue(context.get("eventDate"), LocalDateTime.class),
                        mapper.convertValue(context.get("price"), BigDecimal.class),
                        mapper.convertValue(context.get("currency"), String.class),
                        mapper.convertValue(context.get("orderId"), String.class),
                        mapper.convertValue(context.get("status"), String.class)
                );
            case PAYMENT_VERIFICATION_STARTED:
                return null; // NOT REQUIRED
            case PAYMENT_SUCCESS:
                return new PaymentOrderResponse(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class),
                        mapper.convertValue(context.get("customerId"), Long.class),
                        mapper.convertValue(context.get("username"), String.class),
                        mapper.convertValue(context.get("email"), String.class),
                        mapper.convertValue(context.get("firstName"), String.class),
                        mapper.convertValue(context.get("lastName"), String.class),
                        mapper.convertValue(context.get("eventName"), String.class),
                        mapper.convertValue(context.get("eventDate"), LocalDateTime.class),
                        mapper.convertValue(context.get("price"), BigDecimal.class),
                        mapper.convertValue(context.get("currency"), String.class),
                        mapper.convertValue(context.get("orderId"), String.class),
                        mapper.convertValue("SUCCESS", String.class)
                );
            case PAYMENT_FAILED:
                return new PaymentOrderResponse(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class),
                        mapper.convertValue(context.get("customerId"), Long.class),
                        mapper.convertValue(context.get("username"), String.class),
                        mapper.convertValue(context.get("email"), String.class),
                        mapper.convertValue(context.get("firstName"), String.class),
                        mapper.convertValue(context.get("lastName"), String.class),
                        mapper.convertValue(context.get("eventName"), String.class),
                        mapper.convertValue(context.get("eventDate"), LocalDateTime.class),
                        mapper.convertValue(context.get("price"), BigDecimal.class),
                        mapper.convertValue(context.get("currency"), String.class),
                        mapper.convertValue(context.get("orderId"), String.class),
                        mapper.convertValue("FAILURE", String.class)
                );
            case REGISTRATION_COMPLETED:
                return new PaymentVerifyResponse(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("customerId"), Long.class),
                        mapper.convertValue(context.get("orderId"), String.class),
                        mapper.convertValue(context.get("paymentId"), String.class),
                        mapper.convertValue(context.get("status"), String.class)
                );
            case REGISTRATION_COMPENSATED:
                return new SeatUnreserveResponse(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class),
                        mapper.convertValue(context.get("success"), Boolean.class)
                );
            default:
                return new RegistrationRequest(
                        mapper.convertValue(context.get("registrationId"), String.class),
                        mapper.convertValue(context.get("eventId"), Long.class)
                );
        }
    }
}
