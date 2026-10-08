package com.stsc.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A saved "meaningful visit" - created when a user spends more than a
 * threshold amount of time at one location. When the user later re-enters
 * the geofence around this location, the app resurfaces it as a gentle
 * memory ("Journey Echo").
 */
@Entity
@Table(name = "travel_memory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    private String placeName;

    @Column(length = 1000)
    private String note;

    private String photoUrl;

    @Builder.Default
    private LocalDateTime visitedAt = LocalDateTime.now();

    private LocalDateTime lastResurfacedAt;

    @Builder.Default
    private int timesRevisited = 0;
}
