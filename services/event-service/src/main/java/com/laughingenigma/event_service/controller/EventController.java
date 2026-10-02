package com.laughingenigma.event_service.controller;

import com.laughingenigma.event_service.entity.Event;
import com.laughingenigma.event_service.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping("/{eventId}/reserve")
    public ResponseEntity<Event> reserveSeat(@PathVariable Long eventId, @RequestParam String registrationId) {
        Event event = eventService.reserveSeat(registrationId, eventId);

        return ResponseEntity.ok(event);
    }

    @PostMapping("/{eventId}/release")
    public ResponseEntity<Event> releaseSeat(@PathVariable Long eventId, @RequestParam String registrationId) {
        Event event = eventService.releaseSeat(registrationId, eventId);
        return ResponseEntity.ok(event);
    }
}