package com.laughingenigma.saga_orchestrator.saga.registration_saga;

import com.laughingenigma.saga_orchestrator.config.KafkaConfig;
import com.laughingenigma.saga_orchestrator.config.RabbitMQConfig;
import com.laughingenigma.saga_orchestrator.dto.*;
import com.laughingenigma.saga_orchestrator.entity.*;
import com.laughingenigma.saga_orchestrator.error.exception.ResourceNotFoundException;
import com.laughingenigma.saga_orchestrator.kafka.event.*;
import com.laughingenigma.saga_orchestrator.kafka.producer.KafkaRegistrationEventProducer;
import com.laughingenigma.saga_orchestrator.publisher.*;
import com.laughingenigma.saga_orchestrator.repository.OutboxMessageRepository;
import com.laughingenigma.saga_orchestrator.repository.SagaInstanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
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

    private final SagaInstanceRepository sagaInstanceRepository;

    public RegistrationSaga(
            CustomerValidationRequestPublisher customerValidationRequestPublisher,
            SeatReservationRequestPublisher seatReservationRequestPublisher,
            SeatUnreserveRequestPublisher seatUnreserveRequestPublisher,
            PaymentOrderRequestPublisher paymentOrderRequestPublisher,
            PaymentVerifyRequestPublisher paymentVerifyRequestPublisher,
            SagaInstanceRepository sagaInstanceRepository,
            KafkaRegistrationEventProducer kafkaRegistrationEventProducer,

            OutboxMessageRepository outboxMessageRepository,
            ObjectMapper objectMapper) {
        this.customerValidationRequestPublisher = customerValidationRequestPublisher;
        this.seatReservationRequestPublisher = seatReservationRequestPublisher;
        this.seatUnreserveRequestPublisher = seatUnreserveRequestPublisher;
        this.paymentOrderRequestPublisher = paymentOrderRequestPublisher;
        this.paymentVerifyRequestPublisher = paymentVerifyRequestPublisher;
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.kafkaRegistrationEventProducer = kafkaRegistrationEventProducer;

        this.outboxMessageRepository = outboxMessageRepository;
        this.objectMapper = objectMapper;
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

    public void sendEmailNotification(RegistrationEvent event){
        kafkaRegistrationEventProducer.publish(event);
    }
}
