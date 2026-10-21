package com.laughingenigma.event_service.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "venue-details")
public class VenueDetails {

    @Id
    private ObjectId id;

    private String type;

    // In-person venue
    private String name;
    private String address;

    // Online venue
    private String platform;
    private String accessInstructions;

    // Shared venue property between In-person venue & Online venue
    private String timezone;

    // Hybrid venue references
    private ObjectId inPersonVenueId;
    private ObjectId onlineVenueId;
}