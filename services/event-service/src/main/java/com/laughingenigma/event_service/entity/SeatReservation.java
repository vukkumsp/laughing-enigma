package com.laughingenigma.event_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "seat_reservations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_registration_event",
                        columnNames = {"registration_id", "event_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SeatReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String registrationId;

    @Column(nullable = false)
    private Long eventId;
}
