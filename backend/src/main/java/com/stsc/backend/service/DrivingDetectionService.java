package com.stsc.backend.service;

import com.stsc.backend.config.AppConstants;
import com.stsc.backend.dto.DrivingDetectionRequest;
import org.springframework.stereotype.Service;

/**
 * Decides whether a stream of recent GPS samples represents a genuine
 * driving journey, as opposed to someone walking a short distance.
 *
 * SCOPE NOTE: this is the simplified threshold-based approach described in
 * the project report. A production version would fuse this with Android's
 * ActivityRecognition API (walking / in_vehicle / on_bicycle confidence
 * scores) for much higher accuracy - that is called out as future work.
 */
@Service
public class DrivingDetectionService {

    public boolean isDrivingJourney(DrivingDetectionRequest request) {
        boolean speedOk = request.getAverageSpeedKmh() >= AppConstants.DRIVING_SPEED_THRESHOLD_KMH;
        boolean distanceOk = request.getTotalDistanceMeters() >= AppConstants.MIN_DISTANCE_METERS;
        boolean durationOk = request.getDurationSeconds() >= AppConstants.MIN_DURATION_SECONDS;

        return speedOk && distanceOk && durationOk;
    }
}
