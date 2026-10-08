package com.stsc.backend.dto;

import lombok.Data;

/**
 * Sent periodically by the Android client with the latest GPS samples so the
 * backend (or the client itself, using the same thresholds) can decide
 * whether this looks like a genuine driving journey rather than a walk.
 *
 * Simplified detection strategy used in this academic build:
 *   - average speed over the sample window >= DRIVING_SPEED_THRESHOLD_KMH
 *   - AND total distance covered >= MIN_DISTANCE_METERS
 *   - AND sustained for >= MIN_DURATION_SECONDS
 * (Full sensor-fusion/activity-recognition based detection is noted as
 * future scope in the project report.)
 */
@Data
public class DrivingDetectionRequest {
    private double averageSpeedKmh;
    private double totalDistanceMeters;
    private long durationSeconds;
}
