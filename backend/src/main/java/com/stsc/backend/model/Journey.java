package com.stsc.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A Journey represents one detected driving trip, from the moment the
 * speed/distance/duration threshold confirms "this is a real drive, not a
 * walk" until the user stops moving for a sustained period.
 */
@Entity
@Table(name = "journey")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private double startLat;

    @Column(nullable = false)
    private double startLng;

    private Double endLat;
    private Double endLng;

    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JourneyStatus status = JourneyStatus.ACTIVE;

    // Vehicle type used for THIS specific journey (may differ from the user's
    // default if they have "BOTH" and chose one at trip start)
    @Enumerated(EnumType.STRING)
    private User.VehicleType vehicleTypeUsed;

    // Was the pre-journey safety reminder (helmet/seatbelt) confirmed by the user?
    @Builder.Default
    private boolean safetyCheckConfirmed = false;

    public enum JourneyStatus {
        ACTIVE, COMPLETED
    }
}
