package com.laughingenigma.event_service.service;

import com.laughingenigma.event_service.entity.Event;
import com.laughingenigma.event_service.repository.EventRepository;
import com.laughingenigma.event_service.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventsBatchService {

    private final EventRepository eventRepository;

    EventsBatchService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> fetchEvents(List<Long> eventIds) {
        return eventRepository.findByIdIn(eventIds);
    }
}
