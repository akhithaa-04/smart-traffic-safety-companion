package com.stsc.android.util;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Mirrors the backend's simplified driving-detection thresholds on-device,
 * so the client can decide locally when to call POST /api/journeys/start
 * without waiting on a round trip for every GPS sample.
 *
 * Feed it location samples as they arrive from FusedLocationProviderClient;
 * it keeps a short rolling window and answers isDrivingJourney().
 *
 * SCOPE NOTE: see AppConstants on the backend for the same thresholds and
 * the reasoning behind this simplified (non-sensor-fusion) approach.
 */
public class DrivingDetector {

    private static final double DRIVING_SPEED_THRESHOLD_KMH = 6.0;
    private static final double MIN_DISTANCE_METERS = 150.0;
    private static final long MIN_DURATION_SECONDS = 45;

    private static class Sample {
        final double lat, lng, speedKmh;
        final long timestampSeconds;
        Sample(double lat, double lng, double speedKmh, long timestampSeconds) {
            this.lat = lat; this.lng = lng; this.speedKmh = speedKmh; this.timestampSeconds = timestampSeconds;
        }
    }

    private final Deque<Sample> window = new ArrayDeque<>();

    public void addSample(double lat, double lng, double speedKmh, long timestampSeconds) {
        window.addLast(new Sample(lat, lng, speedKmh, timestampSeconds));
        // Keep only the last 3 minutes of samples
        while (!window.isEmpty() && timestampSeconds - window.peekFirst().timestampSeconds > 180) {
            window.pollFirst();
        }
    }

    public boolean isDrivingJourney() {
        if (window.size() < 2) return false;

        Sample first = window.peekFirst();
        Sample last = window.peekLast();

        long durationSeconds = last.timestampSeconds - first.timestampSeconds;
        double totalDistanceMeters = GeoUtil.distanceMeters(first.lat, first.lng, last.lat, last.lng);
        double avgSpeed = window.stream().mapToDouble(s -> s.speedKmh).average().orElse(0);

        return avgSpeed >= DRIVING_SPEED_THRESHOLD_KMH
                && totalDistanceMeters >= MIN_DISTANCE_METERS
                && durationSeconds >= MIN_DURATION_SECONDS;
    }

    public double currentAverageSpeedKmh() {
        return window.stream().mapToDouble(s -> s.speedKmh).average().orElse(0);
    }

    public void reset() {
        window.clear();
    }
}
