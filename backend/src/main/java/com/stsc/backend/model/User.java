package com.stsc.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    // NOTE: stored as a plain string for this academic project.
    // In production, hash with BCrypt (spring-security-crypto) before saving.
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VehicleType vehicleType = VehicleType.BOTH;

    @Builder.Default
    private int points = 0;

    @Builder.Default
    private int safeStartStreak = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum VehicleType {
        TWO_WHEELER, CAR, BOTH
    }
}
