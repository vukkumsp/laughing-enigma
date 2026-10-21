package com.laughingenigma.event_service.controller;

import com.laughingenigma.event_service.dto.EventDetailsResponse;
import com.laughingenigma.event_service.service.EventDetailsQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
public class EventDetailsController {
    private final EventDetailsQueryService eventDetailsQueryService;

    public EventDetailsController(
            EventDetailsQueryService eventDetailsQueryService) {
        this.eventDetailsQueryService = eventDetailsQueryService;
    }

    @GetMapping("/{eventId}/details")
    public EventDetailsResponse getEventDetails(
            @PathVariable Long eventId) {
        return eventDetailsQueryService.getEventDetails(eventId);
    }
}
