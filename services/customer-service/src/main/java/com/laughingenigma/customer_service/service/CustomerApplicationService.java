package com.laughingenigma.customer_service.service;

import com.laughingenigma.customer_service.dto.CustomerBookingStatusUpdateRequest;
import com.laughingenigma.customer_service.dto.CustomerBookingStatusUpdateResponse;
import com.laughingenigma.customer_service.dto.CustomerValidationRequest;
import com.laughingenigma.customer_service.dto.CustomerValidationResponse;
import com.laughingenigma.customer_service.entity.BookingStatus;
import com.laughingenigma.customer_service.entity.Customer;
import com.laughingenigma.customer_service.entity.CustomerBooking;
import com.laughingenigma.customer_service.error.exception.InvalidBookingStatusException;
import com.laughingenigma.customer_service.publisher.CustomerBookingStatusUpdateResponsePublisher;
import com.laughingenigma.customer_service.publisher.CustomerValidationResponsePublisher;
import org.springframework.stereotype.Service;

@Service
public class CustomerApplicationService {

    private final CustomerService customerService;
    private final CustomerBookingService customerBookingService;
    private final CustomerValidationResponsePublisher publisher;
    private final CustomerBookingStatusUpdateResponsePublisher customerBookingStatusUpdatePublisher;

    public CustomerApplicationService(
            CustomerService customerService,
            CustomerBookingService customerBookingService,
            CustomerValidationResponsePublisher publisher,
            CustomerBookingStatusUpdateResponsePublisher customerBookingStatusUpdatePublisher) {
        this.customerService = customerService;
        this.customerBookingService = customerBookingService;
        this.publisher = publisher;
        this.customerBookingStatusUpdatePublisher = customerBookingStatusUpdatePublisher;
    }

    public void validateCustomerAndRespondToSaga(CustomerValidationRequest customerValidationRequest){
        Customer customer = customerService.validateCustomer(customerValidationRequest.username());
        boolean valid = (customer != null);

        CustomerValidationResponse response = new CustomerValidationResponse(
                customerValidationRequest.registrationId(),
                customerValidationRequest.eventId(),
                valid,
                valid ? customer.getId() : 0L,
                valid ? customer.getUsername() : "",
                valid ? customer.getEmail() : "",
                valid ? customer.getFirstName() : "",
                valid ? customer.getLastName() : "");

        // TODO: save to outbox instead of publishing directly
        // TODO: Once outbox is introduced, make this method transactional
        publisher.publish(response);
    }

    public void createOrUpdateCustomerBookingAndRespondToSaga
            (CustomerBookingStatusUpdateRequest customerBookingStatusUpdateRequest){

        CustomerBooking booking;
        switch (customerBookingStatusUpdateRequest.bookingStatus().toUpperCase()){
            case "PENDING":
                // means, we need to create the db record
                booking = customerBookingService.createCustomerBooking(
                        customerBookingStatusUpdateRequest.username(),
                        customerBookingStatusUpdateRequest.registrationId(),
                        customerBookingStatusUpdateRequest.eventId(),
                        customerBookingStatusUpdateRequest.orderId(),
                        customerBookingStatusUpdateRequest.currency(),
                        customerBookingStatusUpdateRequest.amount()
                );
                break;
            case "CONFIRMED":
            case "CANCELLED":
                booking = customerBookingService.updateCustomerBookingStatus(
                        customerBookingStatusUpdateRequest.registrationId(),
                        customerBookingStatusUpdateRequest.bookingStatus()
                );
                break;
            default:
                System.out.println("Invalid booking status provided");
                throw new InvalidBookingStatusException("Invalid booking status provided");
        }

        CustomerBookingStatusUpdateResponse response = new CustomerBookingStatusUpdateResponse(
                booking.getRegistrationId(),
                booking.getEventId(),
                booking.getCustomerUsername(),
                booking.getOrderId(),
                booking.getCurrency(),
                booking.getAmount(),
                booking.getBookingStatus().name()
        );

        // TODO: save to outbox instead of publishing directly
        // TODO: Once outbox is introduced, make this method transactional
        customerBookingStatusUpdatePublisher.publish(response);
    }
}
