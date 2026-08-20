package com.laughingenigma.event_service.controller;

import com.laughingenigma.event_service.entity.Event;
import com.laughingenigma.event_service.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }
}