package com.stsc.backend.config;

/**
 * Central place for all the tunable thresholds used across the app.
 * Keeping these as named constants (instead of magic numbers scattered
 * through services) makes it easy to explain and defend each number
 * during a project viva.
 */
public final class AppConstants {

    private AppConstants() {}

    // ---- Driving journey detection (simplified GPS-threshold approach) ----
    public static final double DRIVING_SPEED_THRESHOLD_KMH = 6.0;   // above this -> likely driving, not walking
    public static final double MIN_DISTANCE_METERS = 150.0;          // must have actually moved
    public static final long MIN_DURATION_SECONDS = 45;               // sustained for a little while, not a GPS blip

    // ---- Incident report relevance / safety colour ----
    public static final double DEFAULT_PROXIMITY_RADIUS_METERS = 1000.0;
    public static final int REPORT_FRESH_HOURS = 3;     // reports older than this count less toward RED
    public static final int REPORT_EXPIRE_HOURS = 12;    // unconfirmed reports auto-expire after this long
    public static final int RED_CONFIRM_THRESHOLD = 2;   // >= this many net confirms within radius -> RED
    public static final int YELLOW_CONFIRM_THRESHOLD = 1; // >= this many -> YELLOW

    // ---- Rewards ----
    public static final int POINTS_NEW_REPORT = 10;
    public static final int POINTS_REPORT_WITH_PHOTO_BONUS = 5;
    public static final int POINTS_CONFIRM_VALID_REPORT = 3;
    public static final int POINTS_FLAG_CLEARED_REPORT = 3;
    public static final int POINTS_SAFE_START = 2;

    // ---- Travel memory ----
    public static final long MEANINGFUL_VISIT_MINUTES = 20;   // dwell time to count as a "memory"
    public static final double MEMORY_GEOFENCE_RADIUS_METERS = 150.0;
}
