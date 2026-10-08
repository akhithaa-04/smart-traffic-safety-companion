# Android Client (scaffold)

This folder is a **source scaffold**, not a buildable Gradle project out of
the box — it's meant to be opened inside a fresh Android Studio project
(or dropped into one) so you don't start from a blank screen.

## What's here
- `util/DrivingDetector.java` — mirrors the backend's speed/distance/duration
  thresholds so the app can decide locally when a real driving journey has begun.
- `util/GeoUtil.java` — haversine distance helper.
- `service/JourneyTrackingService.java` — foreground service skeleton; wire up
  `FusedLocationProviderClient` and call `onLocationSample(...)` with each fix.
- `service/SafetyReminderPlayer.java` — TextToSpeech wrapper for the rotating
  helmet/seatbelt voice reminders returned by the backend.
- `network/ApiClient.java` — documents every backend endpoint the client calls
  (see `docs/API.md` at the project root for full request/response shapes).
- `AndroidManifest.xml`, `app/build.gradle` — permissions and dependencies
  already set up for location + foreground service + Google Play Services maps.

## To get this running
1. Create a new Android Studio project (Empty Views Activity, Java, minSdk 24).
2. Copy these `java/`, `AndroidManifest.xml`, and merge `build.gradle` into it.
3. Add a `MainActivity` that requests location permissions, starts
   `JourneyTrackingService`, and renders the map (Google Maps SDK) plus the
   citizen-reporting UI.
4. Point `ApiClient.BASE_URL` at your running backend (`10.0.2.2:8080` for the emulator).

## Honest scope note
Full always-on background tracking (surviving a force-close) and true
accelerometer/activity-recognition-based driving detection are **not**
implemented here — see the project abstract's "Scope and Simplifications"
section for why, and what the production upgrade path looks like.
