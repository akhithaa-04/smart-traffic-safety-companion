package com.stsc.android.network;

/**
 * Thin wrapper around the backend REST API described in docs/API.md.
 * Uses plain HttpURLConnection + org.json here to avoid pulling in extra
 * build dependencies for this academic scaffold; swap for Retrofit/OkHttp
 * in a production build if preferred.
 *
 * Base URL: point this at your backend's address.
 *   - Android emulator talking to a backend on your dev machine: http://10.0.2.2:8080
 *   - Physical device on the same Wi-Fi as your dev machine: http://<your-machine-LAN-IP>:8080
 */
public class ApiClient {

    public static final String BASE_URL = "http://10.0.2.2:8080/api";

    // NOTE: This class intentionally only documents the contract used by the
    // rest of the Android scaffold (DrivingDetectionService, SafetyReminderPlayer,
    // etc). Wire it up to your preferred HTTP library:
    //
    //   POST {BASE_URL}/journeys/detect        -> { isDrivingJourney: boolean }
    //   POST {BASE_URL}/journeys/start          -> { journey: {...}, safetyReminder: String }
    //   POST {BASE_URL}/journeys/{id}/confirm-safety-check
    //   POST {BASE_URL}/journeys/{id}/end
    //   POST {BASE_URL}/reports                 -> submit a citizen report
    //   GET  {BASE_URL}/reports/nearby?lat=&lng=&radiusMeters=
    //   GET  {BASE_URL}/reports/safety-color?lat=&lng=&radiusMeters=
    //   POST {BASE_URL}/reports/{id}/respond    -> confirm/dismiss
    //   POST {BASE_URL}/memories                -> save a travel memory
    //   GET  {BASE_URL}/memories/echo?userId=&lat=&lng=
    //
    // Full request/response field names are in docs/API.md at the project root.
}
