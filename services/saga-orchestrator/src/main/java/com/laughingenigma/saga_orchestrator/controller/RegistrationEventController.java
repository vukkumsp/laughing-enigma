package com.laughingenigma.saga_orchestrator.controller;

import com.laughingenigma.saga_orchestrator.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/registrations")
public class RegistrationEventController {

    private final SseService sseService;

    public RegistrationEventController(
            SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(
            value = "/{registrationId}/events",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter events(@PathVariable String registrationId) {
        return sseService.connect(registrationId);
    }

    @PostMapping("/{registrationId}/events/test")
    public void sendTestEvent(@PathVariable String registrationId) {
        sseService.sendTestEvent(registrationId);
    }
}
