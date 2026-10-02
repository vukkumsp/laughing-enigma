package com.laughingenigma.event_service.repository;

import com.laughingenigma.event_service.entity.SeatRelease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReleaseRepository extends JpaRepository<SeatRelease, Long> {
    Optional<SeatRelease> findByRegistrationIdAndEventId(
            String registrationId, Long eventId
    );
}
