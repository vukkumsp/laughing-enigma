package com.laughingenigma.event_service.repository;

import com.laughingenigma.event_service.document.VenueDetails;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VenueDetailsRepository extends MongoRepository<VenueDetails, ObjectId> {
}
