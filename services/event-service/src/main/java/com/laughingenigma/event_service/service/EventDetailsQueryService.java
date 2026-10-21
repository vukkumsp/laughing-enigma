package com.laughingenigma.event_service.service;

import com.laughingenigma.event_service.document.EventDetails;
import com.laughingenigma.event_service.document.VenueDetails;
import com.laughingenigma.event_service.dto.AgendaItemResponse;
import com.laughingenigma.event_service.dto.EventDetailsResponse;
import com.laughingenigma.event_service.dto.HostResponse;
import com.laughingenigma.event_service.dto.VenueResponse;
import com.laughingenigma.event_service.entity.Event;
import com.laughingenigma.event_service.error.exception.ResourceNotFoundException;
import com.laughingenigma.event_service.repository.EventDetailsRepository;
import com.laughingenigma.event_service.repository.EventRepository;
import com.laughingenigma.event_service.repository.VenueDetailsRepository;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventDetailsQueryService {

    private final EventDetailsRepository eventDetailsRepository;
    private final VenueDetailsRepository venueDetailsRepository;
    private final EventRepository eventRepository;

    public EventDetailsQueryService(
            EventDetailsRepository eventDetailsRepository,
            VenueDetailsRepository venueDetailsRepository,
            EventRepository eventRepository) {
        this.eventDetailsRepository = eventDetailsRepository;
        this.venueDetailsRepository = venueDetailsRepository;
        this.eventRepository = eventRepository;
    }

    public EventDetailsResponse getEventDetails(Long eventId) {

        EventDetails details = eventDetailsRepository
                .findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event details not found for eventId: " + eventId
                ));

        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found in PostgreSQL: " + eventId
                ));

        VenueResponse venue = resolveVenue(details.getVenueId());

        EventDetails.Host host = details.getHost();

        HostResponse hostResponse = host == null ? null :
                new HostResponse(
                        host.getName(),
                        host.getTitle(),
                        host.getImageUrl(),
                        host.getBio()
                );

        List<AgendaItemResponse> agenda = details.getAgenda() == null
                ? List.of()
                : details.getAgenda().stream()
                .map(item -> new AgendaItemResponse(
                        item.getTime(),
                        item.getTitle(),
                        item.getDescription()
                ))
                .toList();

        return new EventDetailsResponse(
                details.getEventId(),
                details.getName(),
                details.getDescription(),
                details.getEventDate(),
                details.getCategory(),
                details.getBannerImageUrl(),
                details.getDuration(),
                event.getAvailableSeats(),
                event.getPrice(),
                event.getCurrency().name(),
                venue,
                hostResponse,
                details.getHighlights(),
                agenda
        );
    }

    private VenueResponse resolveVenue(ObjectId venueId) {

        if (venueId == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Event details do not contain a venueId"
            );
        }

        VenueDetails venue = venueDetailsRepository
                .findById(venueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Referenced venue not found: " + venueId
                ));

        if ("hybrid".equalsIgnoreCase(venue.getType())) {

            VenueResponse inPerson = resolveVenue(
                    venue.getInPersonVenueId()
            );

            VenueResponse online = resolveVenue(
                    venue.getOnlineVenueId()
            );

            return new VenueResponse(
                    venue.getType(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    inPerson,
                    online
            );
        }

        return new VenueResponse(
                venue.getType(),
                venue.getName(),
                venue.getAddress(),
                venue.getTimezone(),
                venue.getPlatform(),
                venue.getAccessInstructions(),
                null,
                null
        );
    }
}
