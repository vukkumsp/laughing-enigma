package com.laughingenigma.event_service.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "event-details")
public class EventDetails {

    @Id
    private ObjectId id;

    private Long eventId;
    private String name;
    private String description;
    private String eventDate;
    private String category;
    private String bannerImageUrl;
    private String duration;

    private ObjectId venueId;

    private Host host;
    private List<String> highlights;
    private List<AgendaItem> agenda;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Host {
        private String name;
        private String title;
        private String imageUrl;
        private String bio;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgendaItem {
        private String time;
        private String title;
        private String description;
    }
}