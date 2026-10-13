package com.laughingenigma.saga_orchestrator.saga.registration_saga;

import com.laughingenigma.saga_orchestrator.config.KafkaConfig;
import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.*;
import com.laughingenigma.saga_orchestrator.entity.*;
import com.laughingenigma.saga_orchestrator.error.exception.InvalidBookingStatusException;
import com.laughingenigma.saga_orchestrator.error.exception.ResourceNotFoundException;
import com.laughingenigma.saga_orchestrator.kafka.event.*;
import com.laughingenigma.saga_orchestrator.kafka.producer.KafkaRegistrationEventProducer;
import com.laughingenigma.saga_orchestrator.publisher.*;
import com.laughingenigma.saga_orchestrator.repository.OutboxMessageRepository;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import com.laughingenigma.saga_orchestrator.service.sse.SseService;
import com.laughingenigma.saga_orchestrator.service.sse.dto.PaymentRequiredDetails;
import com.laughingenigma.saga_orchestrator.service.sse.dto.PaymentStatusDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class RegistrationSaga {

    private final CustomerValidationRequestPublisher customerValidationRequestPublisher;
    private final SeatReservationRequestPublisher seatReservationRequestPublisher;
    private final SeatUnreserveRequestPublisher seatUnreserveRequestPublisher;
    private final PaymentOrderRequestPublisher  paymentOrderRequestPublisher;
    private final PaymentVerifyRequestPublisher paymentVerifyRequestPublisher;
    private final KafkaRegistrationEventProducer kafkaRegistrationEventProducer;

    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectMapper objectMapper;
    private final DtoFactory dtoFactory;

    private final SagaInstanceRepository sagaInstanceRepository;

    private final SseService sseService;


    public RegistrationSaga(
            CustomerValidationRequestPublisher customerValidationRequestPublisher,
            SeatReservationRequestPublisher seatReservationRequestPublisher,
            SeatUnreserveRequestPublisher seatUnreserveRequestPublisher,
            PaymentOrderRequestPublisher paymentOrderRequestPublisher,
            PaymentVerifyRequestPublisher paymentVerifyRequestPublisher,
            SagaInstanceRepository sagaInstanceRepository,
            KafkaRegistrationEventProducer kafkaRegistrationEventProducer,

            OutboxMessageRepository outboxMessageRepository,
            ObjectMapper objectMapper,

            DtoFactory dtoFactory,

            SseService sseService) {
        this.customerValidationRequestPublisher = customerValidationRequestPublisher;
        this.seatReservationRequestPublisher = seatReservationRequestPublisher;
        this.seatUnreserveRequestPublisher = seatUnreserveRequestPublisher;
        this.paymentOrderRequestPublisher = paymentOrderRequestPublisher;
        this.paymentVerifyRequestPublisher = paymentVerifyRequestPublisher;
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.kafkaRegistrationEventProducer = kafkaRegistrationEventProducer;

        this.outboxMessageRepository = outboxMessageRepository;
        this.objectMapper = objectMapper;

        this.dtoFactory = dtoFactory;

        this.sseService = sseService;
    }

    @Transactional
    public RegistrationResponse startRegistration(
            String registrationId,
            Long eventId,
            String username) {

        sagaInstanceRepository.save(SagaInstance.builder()
                                                .correlationId(registrationId)
                                                .sagaType(SagaType.REGISTRATION)
                                                .currentStep(SagaStep.REGISTRATION_STARTED)
                                                .status(SagaStatus.STARTED)
                                                .build());

        //Step 1: Validate Customer
        CustomerValidationRequest customerValidationRequest =
                new CustomerValidationRequest(
                        registrationId,
                        eventId,
                        username
                );

        //customerValidationRequestPublisher.publish(customerValidationRequest);

        String payload = objectMapper.writeValueAsString(customerValidationRequest);

        OutboxMessage outboxMessage = OutboxMessage.builder()
                .outboxMessageId(UUID.randomUUID())
                .destinationType(DestinationType.RABBITMQ)
                .messageType(OutboxMessageType.CUSTOMER_VALIDATION_REQUEST)
                .destinationName(RabbitMQConfig.SAGA_COMMAND_EXCHANGE)
                .routingKey(RabbitMQConfig.CUSTOMER_VALIDATION_REQUEST_ROUTING_KEY)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .retryCount(0)
                .nextRetryAt(null)
                .build();

        outboxMessageRepository.save(outboxMessage);

        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(registrationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username " + username + " was not found"
                        )
                );

        sagaI.setCurrentStep(SagaStep.CUSTOMER_VALIDATION);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaInstanceRepository.save(sagaI);

        System.out.println("startRegistration - "+registrationId);
        return new RegistrationResponse(
                registrationId,
                eventId,
                SagaStep.CUSTOMER_VALIDATION.name()
        );
    }

    @Transactional
    public void reserveSeatsForRegistration(
            CustomerValidationResponse response) {
        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with response.registrationId() " + response.registrationId() + " was not found"
                        )
                );

        if (!response.valid()) {
            // Saga failed
            sagaI.setCurrentStep(SagaStep.CUSTOMER_VALIDATION_FAILED);
            sagaI.setStatus(SagaStatus.IN_PROGRESS);
            sagaInstanceRepository.save(sagaI);
            return;
        }

        SeatReservationRequest seatReservationRequest =
                new SeatReservationRequest(
                        response.registrationId(),
                        response.eventId(),
                        response.customerId(),
                        response.username(),
                        response.email(),
                        response.firstName(),
                        response.lastName()
                );

        // Customer validation succeeded.
        // Start Step 2.
        //seatReservationRequestPublisher.publish(seatReservationRequest);

        String payload = objectMapper.writeValueAsString(seatReservationRequest);

        OutboxMessage outboxMessage = OutboxMessage.builder()
                .outboxMessageId(UUID.randomUUID())
                .destinationType(DestinationType.RABBITMQ)
                .messageType(OutboxMessageType.SEAT_RESERVATION_REQUEST)
                .destinationName(RabbitMQConfig.SAGA_COMMAND_EXCHANGE)
                .routingKey(RabbitMQConfig.SEAT_RESERVATION_REQUEST_ROUTING_KEY)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .retryCount(0)
                .nextRetryAt(null)
                .build();

        outboxMessageRepository.save(outboxMessage);

        sagaI.setCurrentStep(SagaStep.SEAT_RESERVATION);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaInstanceRepository.save(sagaI);

        System.out.println("reserveSeatsForRegistration - "+response.registrationId());
    }

    @Transactional
    public void initiatePaymentOrder(SeatReservationResponse response) {
        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with response.registrationId() " + response.registrationId() + " was not found"
                        )
                );

        if (sagaI.getCurrentStep() == SagaStep.SEAT_RESERVED) {
            System.out.println(
                    "IDEMPOTENCY: Duplicate seat reservation response ignored. "
                            + "registrationId=" + response.registrationId()
            );
            return;
        }

        if (!response.success()) {
            // Saga failed
            if (sagaI.getCurrentStep() == SagaStep.SEAT_RESERVATION_FAILED) {
                // Duplicate failure response
                System.out.println(
                        "IDEMPOTENCY: Duplicate seat reservation failed response ignored. "
                                + "registrationId=" + response.registrationId()
                );
                return;
            }
            if (!sagaI.getCurrentStep()
                    .canTransitionTo(SagaStep.SEAT_RESERVATION_FAILED)) {
                // Duplicate / stale / invalid response
                System.out.println(
                        "SAGA TRANSITION REJECTED: Cannot transition from " +
                                sagaI.getCurrentStep() +
                                " -> " +
                                SagaStep.SEAT_RESERVATION_FAILED +
                                ", registrationId=" +
                                response.registrationId()
                );
                return;
            }
            sagaI.setCurrentStep(SagaStep.SEAT_RESERVATION_FAILED);
            sagaI.setStatus(SagaStatus.IN_PROGRESS);
            sagaInstanceRepository.save(sagaI);
            return;
        }

        // Validate that current state is one in which
        // SEAT_RESERVED is a valid transition.
        if (!sagaI.getCurrentStep()
                .canTransitionTo(SagaStep.SEAT_RESERVED)) {
            // Duplicate / stale / invalid response
            System.out.println(
                    "SAGA TRANSITION REJECTED: Cannot transition from " +
                            sagaI.getCurrentStep() +
                            " -> " +
                            SagaStep.SEAT_RESERVED +
                            ", registrationId=" +
                            response.registrationId()
            );
            return;
        }

        sagaI.setCurrentStep(SagaStep.SEAT_RESERVED);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaInstanceRepository.save(sagaI);

        PaymentOrderRequest paymentOrderRequest = new  PaymentOrderRequest(
                response.registrationId(),
                response.eventId(),

                response.customerId(),
                response.username(),
                response.email(),
                response.firstName(),
                response.lastName(),

                response.eventName(),
                response.eventDate(),
                response.price(),
                response.currency()
        );

        // with PaymentOrderRequest publication.

        //paymentOrderRequestPublisher.publish(paymentOrderRequest);

        String payload = objectMapper.writeValueAsString(paymentOrderRequest);

        OutboxMessage outboxMessage = OutboxMessage.builder()
                .outboxMessageId(UUID.randomUUID())
                .destinationType(DestinationType.RABBITMQ)
                .messageType(OutboxMessageType.PAYMENT_ORDER_REQUEST)
                .destinationName(RabbitMQConfig.SAGA_COMMAND_EXCHANGE)
                .routingKey(RabbitMQConfig.PAYMENT_ORDER_REQUEST_ROUTING_KEY)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .retryCount(0)
                .nextRetryAt(null)
                .build();

        outboxMessageRepository.save(outboxMessage);
    }

    public void checkPaymentOrderAndUpdateCustomerBooking(PaymentOrderResponse response){
        SagaInstance sagaI = sagaInstanceRepository
                .findByCorrelationId(response.registrationId())
                .orElseThrow();

        if (sagaI.getCurrentStep() == SagaStep.PAYMENT_REQUIRED) {
            System.out.println(
                    "IDEMPOTENCY: Duplicate payment order response ignored. "
                            + "registrationId=" + response.registrationId()
            );
            return;
        }

        if (!sagaI.getCurrentStep()
                .canTransitionTo(SagaStep.PAYMENT_REQUIRED)) {
            // Duplicate / stale / invalid response
            System.out.println(
                    "SAGA TRANSITION REJECTED: "
                            + sagaI.getCurrentStep()
                            + " -> "
                            + SagaStep.PAYMENT_REQUIRED
                            + ", registrationId="
                            + response.registrationId()
            );
            return;
        }

        System.out.println("Payment Order response: "+response);

        sagaI.setCurrentStep(SagaStep.PAYMENT_REQUIRED);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaI.setContext(ContextFactory.buildPaymentOrderResponseContext(response));
        sagaInstanceRepository.save(sagaI);

        // update customer bookings status
        // TODO: move this direct publisher call to outbox pattern
        CustomerBookingStatusUpdateRequest request = new CustomerBookingStatusUpdateRequest(
                response.registrationId(), response.eventId(), response.username(),
                response.orderId(),response.currency(), response.price(),
                "PENDING"
        );

        //TODO: add to outbox_messages
        String payload = objectMapper.writeValueAsString(request);

        OutboxMessage outboxMessage = OutboxMessage.builder()
                .outboxMessageId(UUID.randomUUID())
                .destinationType(DestinationType.RABBITMQ)
                .messageType(OutboxMessageType.CUSTOMER_BOOKING_REQUEST)
                .destinationName(RabbitMQConfig.SAGA_COMMAND_EXCHANGE)
                .routingKey(RabbitMQConfig.CUSTOMER_BOOKING_STATUS_UPDATE_REQUEST_ROUTING_KEY)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .retryCount(0)
                .nextRetryAt(null)
                .build();

        outboxMessageRepository.save(outboxMessage);
    }

    @Transactional
    public void checkPaymentVerifyResponseAndUpdateCustomerBooking(PaymentVerifyResponse response){
        System.out.println("========== PAYMENT VERIFY RESPONSE ==========");
        System.out.println("response = " + response);
        System.out.println("registrationId = [" + response.registrationId() + "]");

        SagaInstance sagaI = sagaInstanceRepository
                .findByCorrelationId(response.registrationId())
                .orElseThrow();

        System.out.println("Payment Verify response: "+response);
        System.out.println("sagaI.getCurrentStep(): " + sagaI.getCurrentStep());
        System.out.println("sagaI.getContext(): "+ sagaI.getContext());

        // PaymentOrderResponse
        PaymentOrderResponse paymentOrderResponse;

        if (response.status().equalsIgnoreCase("success")) {
            //success but duplicated
            if (sagaI.getCurrentStep() == SagaStep.REGISTRATION_COMPLETED) {
                // duplicate
                System.out.println(
                        "IDEMPOTENCY: Duplicate payment success response ignored. "
                                + "registrationId=" + response.registrationId()
                );
                return;
            }

            if (!sagaI.getCurrentStep()
                    .canTransitionTo(SagaStep.PAYMENT_SUCCESS)) {
                // stale/invalid response
                System.out.println(
                        "SAGA TRANSITION REJECTED: "
                                + sagaI.getCurrentStep()
                                + " -> "
                                + SagaStep.PAYMENT_SUCCESS
                                + ", registrationId="
                                + response.registrationId()
                );
                return;
            }
            // process success
        }

        if(!response.status().equalsIgnoreCase("success")) {
            //failure flow
            if (sagaI.getCurrentStep() == SagaStep.PAYMENT_FAILED
                    || sagaI.getCurrentStep() == SagaStep.REGISTRATION_COMPENSATED) {

                System.out.println(
                        "IDEMPOTENCY: Duplicate payment failure response ignored. "
                                + "registrationId=" + response.registrationId()
                );
                return;
            }

            if (!sagaI.getCurrentStep()
                    .canTransitionTo(SagaStep.PAYMENT_FAILED)) {
                // Duplicate / stale / invalid response
                System.out.println(
                        "SAGA TRANSITION REJECTED: " +
                                sagaI.getCurrentStep() +
                                " -> " +
                                SagaStep.PAYMENT_FAILED +
                                ", registrationId=" +
                                response.registrationId()
                );
                return;
            }

            paymentOrderResponse
                    = (PaymentOrderResponse) dtoFactory.getRegistrationContextDto(
                    SagaStep.PAYMENT_FAILED ,
                    sagaI.getContext());

            sagaI.setCurrentStep(SagaStep.PAYMENT_FAILED);
            sagaI.setStatus(SagaStatus.FAILED);
            sagaInstanceRepository.save(sagaI);

            //update customer booking status to CANCELLED
            CustomerBookingStatusUpdateRequest request = new CustomerBookingStatusUpdateRequest(
                    response.registrationId(), paymentOrderResponse.eventId(), paymentOrderResponse.username(),
                    response.orderId(),paymentOrderResponse.currency(), paymentOrderResponse.price(),
                    "CANCELLED"
            );

            String payload = objectMapper.writeValueAsString(request);

            OutboxMessage outboxMessage = OutboxMessage.builder()
                    .outboxMessageId(UUID.randomUUID())
                    .destinationType(DestinationType.RABBITMQ)
                    .messageType(OutboxMessageType.CUSTOMER_BOOKING_REQUEST)
                    .destinationName(RabbitMQConfig.SAGA_COMMAND_EXCHANGE)
                    .routingKey(RabbitMQConfig.CUSTOMER_BOOKING_STATUS_UPDATE_REQUEST_ROUTING_KEY)
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .createdAt(Instant.now())
                    .retryCount(0)
                    .nextRetryAt(null)
                    .build();

            outboxMessageRepository.save(outboxMessage);
            //publisher.publish(request);
            return;
        }

        paymentOrderResponse
                = (PaymentOrderResponse) dtoFactory.getRegistrationContextDto(
                SagaStep.PAYMENT_SUCCESS,
                sagaI.getContext());

        sagaI.setCurrentStep(SagaStep.PAYMENT_SUCCESS);
        sagaI.setStatus(SagaStatus.IN_PROGRESS);
        sagaInstanceRepository.save(sagaI);

        //update customer booking status to CONFIRMED
        CustomerBookingStatusUpdateRequest request = new CustomerBookingStatusUpdateRequest(
                response.registrationId(), paymentOrderResponse.eventId(), paymentOrderResponse.username(),
                response.orderId(),paymentOrderResponse.currency(), paymentOrderResponse.price(),
                "CONFIRMED"
        );

        String payload = objectMapper.writeValueAsString(request);

        OutboxMessage outboxMessage = OutboxMessage.builder()
                .outboxMessageId(UUID.randomUUID())
                .destinationType(DestinationType.RABBITMQ)
                .messageType(OutboxMessageType.CUSTOMER_BOOKING_REQUEST)
                .destinationName(RabbitMQConfig.SAGA_COMMAND_EXCHANGE)
                .routingKey(RabbitMQConfig.CUSTOMER_BOOKING_STATUS_UPDATE_REQUEST_ROUTING_KEY)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .retryCount(0)
                .nextRetryAt(null)
                .build();

        outboxMessageRepository.save(outboxMessage);
        //publisher.publish(request);
    }

    @Transactional
    public void unreserveSeatsAsCompensation(PaymentOrderResponse response) {
        SagaInstance sagaI = sagaInstanceRepository.findByCorrelationId(response.registrationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with response.registrationId() " + response.registrationId() + " was not found"
                        )
                );

        SeatUnreserveRequest seatUnreserveRequest =
                new SeatUnreserveRequest(
                        response.registrationId(),
                        response.eventId(),

                        response.customerId(),
                        response.username(),
                        response.email(),
                        response.firstName(),
                        response.lastName(),

                        response.eventName(),
                        response.eventDate()
                );

        sagaI.setCurrentStep(SagaStep.SEAT_RELEASING);
        sagaI.setStatus(SagaStatus.COMPENSATING);
        sagaInstanceRepository.save(sagaI);

        // with SeatUnreserveRequest publication.
        //seatUnreserveRequestPublisher.publish(seatUnreserveRequest);

        String payload = objectMapper.writeValueAsString(seatUnreserveRequest);

        OutboxMessage outboxMessage = OutboxMessage.builder()
                .outboxMessageId(UUID.randomUUID())
                .destinationType(DestinationType.RABBITMQ)
                .messageType(OutboxMessageType.SEAT_UNRESERVE_REQUEST)
                .destinationName(RabbitMQConfig.SAGA_COMMAND_EXCHANGE)
                .routingKey(RabbitMQConfig.SEAT_UNRESERVE_REQUEST_ROUTING_KEY)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .retryCount(0)
                .nextRetryAt(null)
                .build();

        outboxMessageRepository.save(outboxMessage);

        System.out.println("unreserveSeatsAsCompensation - "+response.registrationId());

        //send email notification
        UUID kafkaEventId = UUID.randomUUID();
        EventDetails eventDetails = new EventDetails(
                response.eventId(),
                response.eventName(),
                response.eventDate().toInstant(ZoneOffset.UTC),
                Instant.now()
        );
        RegistrationEventPayload KafkaPayload = new RegistrationFailedPayload(
                response.registrationId(),
                response.username(),
                response.email(),
                eventDetails,
                "Payment Failed"
        );
        RegistrationEvent event = new RegistrationEvent(
                kafkaEventId,
                KafkaConfig.REGISTRATION_FAILED,
                1,
                Instant.now(),
                KafkaConfig.APPLICATION_NAME,
                KafkaPayload
        );

        // TODO: Introduce Outbox Pattern to reliably coordinate
        // compensation state change and REGISTRATION_FAILED Kafka publication.
        //sendEmailNotification(event);

        String kafkaEventPayload = objectMapper.writeValueAsString(event);

        OutboxMessage kafkaOutboxMessage = OutboxMessage.builder()
                .outboxMessageId(UUID.randomUUID())
                .destinationType(DestinationType.KAFKA)
                .messageType(OutboxMessageType.REGISTRATION_FAILED)
                .destinationName(KafkaConfig.REGISTRATION_EVENTS_TOPIC)
                .routingKey(null)
                .payload(kafkaEventPayload)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .retryCount(0)
                .nextRetryAt(null)
                .build();

        outboxMessageRepository.save(kafkaOutboxMessage);
    }

    public void handleCustomerBookingStatusUpdateResponse(CustomerBookingStatusUpdateResponse response) {
        //
        SagaInstance sagaI = sagaInstanceRepository
                .findByCorrelationId(response.registrationId())
                .orElseThrow();
        switch(response.bookingStatus().toUpperCase()) {
            case "PENDING":
                //send SSE for payment required.
                PaymentRequiredDetails paymentRequiredDetails = new PaymentRequiredDetails(
                        response.registrationId(),
                        response.eventId(),
//-----------------------------------
                        0L,
                        response.username(),
                        "",
                        "",
                        "",
//-----------------------------------
                        "",
                        LocalDateTime.now(),
                        response.amount(),
                        response.currency(),
                        response.orderId(),
                        response.bookingStatus()
                );

                //TODO: move this SSE call to outbox style pattern for reliability
                sseService.sendPaymentRequiredEvent(paymentRequiredDetails);
                break;
            case "CONFIRMED":
                //close saga
                PaymentOrderResponse paymentOrderSuccessResponse
                        = (PaymentOrderResponse) dtoFactory.getRegistrationContextDto(
                        SagaStep.PAYMENT_SUCCESS,
                        sagaI.getContext());

                //IF payment verification is successful then,
                //update status in saga db if needed
                //send SSE event to frontend payment status
                PaymentStatusDetails paymentSuccessStatusDetails = new PaymentStatusDetails(
                        paymentOrderSuccessResponse.registrationId(),
                        paymentOrderSuccessResponse.eventId(),
                        paymentOrderSuccessResponse.status()
                );
                sseService.sendPaymentStatusEvent(paymentSuccessStatusDetails);

                //End of SAGA
                sagaI.setCurrentStep(SagaStep.REGISTRATION_COMPLETED);
                sagaI.setStatus(SagaStatus.COMPLETED);
                sagaInstanceRepository.save(sagaI);

                //send email notification
                UUID kafkaEventId = UUID.randomUUID();

                EventDetails eventDetails = new EventDetails(
                        paymentOrderSuccessResponse.eventId(),
                        paymentOrderSuccessResponse.eventName(),
                        paymentOrderSuccessResponse.eventDate().toInstant(ZoneOffset.UTC),
                        Instant.now()
                );
                RegistrationEventPayload payload = new RegistrationCompletedPayload(
                        paymentOrderSuccessResponse.registrationId(),
                        paymentOrderSuccessResponse.username(),
                        paymentOrderSuccessResponse.email(),
                        eventDetails
                );
                RegistrationEvent event = new RegistrationEvent(
                        kafkaEventId,
                        KafkaConfig.REGISTRATION_COMPLETED,
                        1,
                        Instant.now(),
                        KafkaConfig.APPLICATION_NAME,
                        payload
                );

                // TODO: Introduce Outbox Pattern to reliably coordinate
                // saga completion and REGISTRATION_COMPLETED Kafka publication.
                //registrationSaga.sendEmailNotification(event);

                String kafkaEventPayload = objectMapper.writeValueAsString(event);

                OutboxMessage kafkaOutboxMessage = OutboxMessage.builder()
                        .outboxMessageId(UUID.randomUUID())
                        .destinationType(DestinationType.KAFKA)
                        .messageType(OutboxMessageType.REGISTRATION_COMPLETED)
                        .destinationName(KafkaConfig.REGISTRATION_EVENTS_TOPIC)
                        .routingKey(null)
                        .payload(kafkaEventPayload)
                        .status(OutboxStatus.PENDING)
                        .createdAt(Instant.now())
                        .retryCount(0)
                        .nextRetryAt(null)
                        .build();

                outboxMessageRepository.save(kafkaOutboxMessage);
                break;
            case "CANCELLED":
                //compensate or recover
                //compensation step


                PaymentOrderResponse paymentOrderFailedResponse
                        = (PaymentOrderResponse) dtoFactory.getRegistrationContextDto(
                        SagaStep.PAYMENT_FAILED ,
                        sagaI.getContext());

                unreserveSeatsAsCompensation(paymentOrderFailedResponse);

                PaymentStatusDetails paymentFailedStatusDetails = new PaymentStatusDetails(
                        paymentOrderFailedResponse.registrationId(),
                        paymentOrderFailedResponse.eventId(),
                        paymentOrderFailedResponse.status()
                );

                sseService.sendPaymentStatusEvent(paymentFailedStatusDetails);
                break;
            default:
                //unknown booking status response
                System.out.println("Invalid booking status received in CustomerBookingStatusUpdateResponse");
                throw new InvalidBookingStatusException("Invalid booking status received in CustomerBookingStatusUpdateResponse");
        }
    }
}
