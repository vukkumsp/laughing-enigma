package com.laughingenigma.customer_service.repository;

import com.laughingenigma.customer_service.entity.BookingStatus;
import com.laughingenigma.customer_service.entity.Customer;
import com.laughingenigma.customer_service.entity.CustomerBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerBookingRepository extends JpaRepository<CustomerBooking, Long> {
    List<CustomerBooking> findByCustomerUsername(String customerUsername);
    Optional<CustomerBooking> findByRegistrationId(String registrationId);

    boolean existsByRegistrationIdAndBookingStatus(String registrationId, BookingStatus bookingStatus);
}
