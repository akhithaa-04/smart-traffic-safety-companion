package com.stsc.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A citizen-submitted report about something happening on the road
 * (accident, construction, waterlogging, festival/procession, fallen tree,
 * unexpected blockage, etc). Reports are a SUPPLEMENTARY layer on top of
 * normal map navigation, never the primary routing source.
 */
@Entity
@Table(name = "incident_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    @JsonIgnore
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentType type;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String cause;

    private String photoUrl;

    // How long the citizen expects this to last
    private Integer estimatedDurationMinutes;

    @Column(length = 500)
    private String suggestedAlternateRoute;

    @Builder.Default
    private int confirmCount = 0;

    @Builder.Default
    private int dismissCount = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportStatus status = ReportStatus.ACTIVE;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastConfirmedAt;

    public enum IncidentType {
        ACCIDENT, ROAD_CONSTRUCTION, WATERLOGGING, FESTIVAL_OR_PROCESSION,
        FALLEN_TREE, ROAD_BLOCKAGE, HEAVY_CONGESTION, OTHER
    }

    public enum ReportStatus {
        ACTIVE, EXPIRED, CLEARED
    }
}
