package com.laughingenigma.event_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private LocalDateTime eventDate;
    private int availableSeats;


    @Column(nullable = false, length = 4)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "price", precision = 19, scale = 2, nullable = false)
    private BigDecimal price;
}
