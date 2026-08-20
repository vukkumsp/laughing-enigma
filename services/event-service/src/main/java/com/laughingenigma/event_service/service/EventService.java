package com.laughingenigma.event_service.service;

import com.laughingenigma.event_service.entity.Event;
import com.laughingenigma.event_service.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event reserveSeat(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats available");
        }

        event.setAvailableSeats(event.getAvailableSeats() - 1);

        return eventRepository.save(event);
    }

    public Event releaseSeat(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats available");
        }

        event.setAvailableSeats(event.getAvailableSeats() + 1);
        return eventRepository.save(event);
    }
}