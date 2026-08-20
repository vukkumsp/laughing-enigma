package com.laughingenigma.event_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private LocalDateTime eventDate;
    private int availableSeats;
}
