package com.laughingenigma.event_service.repository;

import com.laughingenigma.event_service.entity.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<SeatReservation, Long> {
    Optional<SeatReservation> findByRegistrationIdAndEventId(
            String registrationId, Long eventId
    );
}
