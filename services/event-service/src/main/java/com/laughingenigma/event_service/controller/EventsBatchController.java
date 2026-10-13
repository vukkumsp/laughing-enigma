package com.laughingenigma.event_service.controller;

import com.laughingenigma.event_service.entity.Event;
import com.laughingenigma.event_service.repository.ReservationRepository;
import com.laughingenigma.event_service.service.EventsBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventsBatchController {

    private final EventsBatchService eventsBatchService;

    public EventsBatchController(EventsBatchService eventsBatchService) {
        this.eventsBatchService = eventsBatchService;
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Event>> fetchEventsBatch(@RequestBody List<Long> eventIds) {
        List<Event> events = eventsBatchService.fetchEvents(eventIds);
        return ResponseEntity.ok(events);
    }
}
