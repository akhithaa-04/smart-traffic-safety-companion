package com.stsc.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    // Simple threshold this badge is awarded at (points, streak, or report count
    // depending on criteriaType) - keeps badge logic data-driven rather than hardcoded.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriteriaType criteriaType;

    @Column(nullable = false)
    private int threshold;

    public enum CriteriaType {
        POINTS, SAFE_START_STREAK, CONFIRMED_REPORT_COUNT
    }
}
