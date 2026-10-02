package com.laughingenigma.event_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "seat_releases",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_release_registration_event",
                columnNames = {"registration_id", "event_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class SeatRelease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String registrationId;

    @Column(nullable = false)
    private Long eventId;
}
