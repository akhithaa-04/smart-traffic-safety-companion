package com.stsc.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.stsc.android.util.DrivingDetector;

/**
 * Foreground Service that runs while the app is open or minimised, polling
 * location updates and feeding them into DrivingDetector. Once a genuine
 * driving journey is detected, it calls the backend to start the journey
 * and triggers the pre-journey safety reminder (voice + notification).
 *
 * SCOPE NOTE (see project report): this runs as a foreground service while
 * the app process is alive, not as a fully persistent OS-level background
 * service that survives the app being force-closed. A production build
 * would additionally request ACCESS_BACKGROUND_LOCATION and handle battery
 * optimisation exemptions - flagged as future work.
 *
 * Wire this up with FusedLocationProviderClient (Google Play Services) to
 * receive real location callbacks and call onLocationSample() below.
 */
public class JourneyTrackingService extends Service {

    private final DrivingDetector drivingDetector = new DrivingDetector();
    private Long activeJourneyId = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null; // not a bound service
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: start foreground notification (required for Android 8+ foreground services)
        // TODO: register FusedLocationProviderClient location callback -> onLocationSample(...)
        return START_STICKY;
    }

    /**
     * Call this from your location callback with each new GPS fix.
     * speedKmh and timestampSeconds come straight off the Android Location object.
     */
    public void onLocationSample(double lat, double lng, double speedKmh, long timestampSeconds) {
        drivingDetector.addSample(lat, lng, speedKmh, timestampSeconds);

        if (activeJourneyId == null && drivingDetector.isDrivingJourney()) {
            // TODO: call POST /api/journeys/start via ApiClient, store returned journey id
            //       in activeJourneyId, then hand the returned safetyReminder string to
            //       SafetyReminderPlayer.speakAndNotify(...)
        }

        // TODO: while activeJourneyId != null, periodically call
        //       GET /api/reports/nearby and /api/reports/safety-color with the latest
        //       lat/lng, and surface "Citizen-reported incident ahead" notifications
        //       plus GET /api/memories/echo to check for a Journey Echo at this spot.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        drivingDetector.reset();
    }
}
