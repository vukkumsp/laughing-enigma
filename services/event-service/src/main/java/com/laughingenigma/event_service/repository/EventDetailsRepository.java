package com.laughingenigma.event_service.repository;

import com.laughingenigma.event_service.document.EventDetails;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EventDetailsRepository
        extends MongoRepository<EventDetails, ObjectId> {

    Optional<EventDetails> findByEventId(Long eventId);
}