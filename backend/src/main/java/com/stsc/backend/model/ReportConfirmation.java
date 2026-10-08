package com.stsc.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records a single user's confirm/dismiss action on an IncidentReport.
 * Keeping these individually (rather than just incrementing a counter)
 * prevents the same user from confirming or dismissing a report twice,
 * and gives an audit trail for the reward system.
 */
@Entity
@Table(name = "report_confirmation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"report_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private IncidentReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ActionType {
        CONFIRM, DISMISS
    }
}
