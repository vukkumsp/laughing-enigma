package com.laughingenigma.customer_service.service;

import com.laughingenigma.customer_service.entity.BookingStatus;
import com.laughingenigma.customer_service.entity.Currency;
import com.laughingenigma.customer_service.entity.CustomerBooking;
import com.laughingenigma.customer_service.error.exception.InvalidBookingStatusException;
import com.laughingenigma.customer_service.error.exception.ResourceNotFoundException;
import com.laughingenigma.customer_service.repository.CustomerBookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerBookingService {
    private final CustomerBookingRepository customerBookingRepository;

    public CustomerBookingService(CustomerBookingRepository customerBookingRepository) {
        this.customerBookingRepository = customerBookingRepository;
    }

    public List<CustomerBooking> getCustomerBookings(String username){
        return this.customerBookingRepository.findByCustomerUsername(username);
    }

    public boolean existsByCustomerBookingByRegistrationIdAndBookingStatus(String registrationId, BookingStatus bookingStatus){
        return this.customerBookingRepository.existsByRegistrationIdAndBookingStatus(registrationId, bookingStatus);
    }

    @Transactional
    public CustomerBooking updateCustomerBookingStatus(String registrationId, String status){
        CustomerBooking booking = customerBookingRepository.findByRegistrationId(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Booking not found for registrationId: " + registrationId));

        switch (status.toUpperCase()){
            case "PENDING":
                booking.setBookingStatus(BookingStatus.PENDING);
                break;
            case "CONFIRMED":
                booking.setBookingStatus(BookingStatus.CONFIRMED);
                break;
            case "CANCELLED":
                booking.setBookingStatus(BookingStatus.CANCELLED);
                break;
            default:
                System.out.println("Invalid booking status provided");
                throw new InvalidBookingStatusException("Invalid booking status provided");
        }
        customerBookingRepository.save(booking);

        return booking;
    }

    @Transactional
    public CustomerBooking createCustomerBooking(
            String customerUsername, String registrationId,
            Long eventId, String orderId, Currency currency, BigDecimal amount
    ){

        CustomerBooking booking = new CustomerBooking();
        booking.setCustomerUsername(customerUsername);
        booking.setRegistrationId(registrationId);
        booking.setEventId(eventId);
        booking.setOrderId(orderId);
        booking.setCurrency(currency);
        booking.setAmount(amount);
        booking.setBookingStatus(BookingStatus.PENDING);

        customerBookingRepository.save(booking);
        return booking;
    }
}
