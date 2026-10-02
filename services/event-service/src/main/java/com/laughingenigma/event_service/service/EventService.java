package com.laughingenigma.event_service.service;

import com.laughingenigma.event_service.entity.Event;
import com.laughingenigma.event_service.entity.SeatRelease;
import com.laughingenigma.event_service.entity.SeatReservation;
import com.laughingenigma.event_service.error.exception.ResourceNotFoundException;
import com.laughingenigma.event_service.repository.EventRepository;
import com.laughingenigma.event_service.repository.ReleaseRepository;
import com.laughingenigma.event_service.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ReservationRepository  reservationRepository;
    private final ReleaseRepository  releaseRepository;

    public EventService(
            EventRepository eventRepository,
            ReservationRepository reservationRepository,
            ReleaseRepository releaseRepository) {
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
        this.releaseRepository = releaseRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }


    @Transactional
    public Event reserveSeat(String registrationId, Long eventId) {

        Optional<SeatReservation> seatReservation =
                reservationRepository.findByRegistrationIdAndEventId(registrationId, eventId);

        if(seatReservation.isPresent()) {
            // Already reserved
            System.out.println(
                    "IDEMPOTENCY CHECK: Duplicate seat reservation ignored. "
                            + "registrationId=" + registrationId
                            + ", eventId=" + eventId
            );
            return eventRepository.findById(eventId)
                    .orElseThrow();
        }

        Event event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event with eventId " + eventId + " was not found"
                        )
                );

        if (event.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats available");
        }

        event.setAvailableSeats(event.getAvailableSeats() - 1);
        eventRepository.save(event);

        SeatReservation reservation = new SeatReservation();
        reservation.setRegistrationId(registrationId);
        reservation.setEventId(eventId);
        reservationRepository.save(reservation);

        return event;
    }

    @Transactional
    public Event releaseSeat(String registrationId, Long eventId) {

        Optional<SeatRelease> seatRelease =
                releaseRepository.findByRegistrationIdAndEventId(
                        registrationId, eventId
                );

        if(seatRelease.isPresent()) {
            // Already released
            System.out.println(
                    "IDEMPOTENCY CHECK: Duplicate seat release ignored. "
                            + "registrationId=" + registrationId
                            + ", eventId=" + eventId
            );
            return eventRepository.findById(eventId)
                    .orElseThrow();
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats available");
        }

        event.setAvailableSeats(event.getAvailableSeats() + 1);
        eventRepository.save(event);

        SeatRelease  release = new SeatRelease();
        release.setRegistrationId(registrationId);
        release.setEventId(eventId);
        releaseRepository.save(release);

        return event;
    }
}