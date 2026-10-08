# Smart Traffic Safety Companion

A full-stack Java project: a proactive travel companion that detects real
driving journeys, gives engaging voice-based pre-journey safety reminders,
layers citizen-reported incidents on top of normal map navigation, rewards
good-faith community reporting with points and badges, and remembers places
you've spent meaningful time so it can resurface them later.

This repo has three parts:

```
backend/         Spring Boot REST API (Java) - the real, working core of the project
frontend-web/    A plain HTML/CSS/JS demo client - run this to SEE the whole system work
android-client/  Source scaffold for the real mobile client (see its own README)
docs/            API reference, database schema
```

## Quick start (backend + web demo)

### 1. Run the backend
```bash
cd backend
mvn spring-boot:run
```
This starts the API on `http://localhost:8080` using an **in-memory H2
database** (zero setup) and automatically seeds demo data: 3 users, 3
badges, and 2 citizen incident reports near Jubilee Hills, Hyderabad.

To use PostgreSQL instead, see the commented-out block in
`backend/src/main/resources/application.properties`.

You can browse the seeded data directly at `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:stscdb`, user `sa`, no password).

### 2. Open the web demo
Just open `frontend-web/index.html` directly in a browser (or serve the
folder with any static file server). It talks to the backend running on
`localhost:8080`.

Demo logins (seeded automatically):
| Email | Password |
|---|---|
| asha@example.com | password123 |
| vikram@example.com | password123 |
| citizen@example.com | password123 |

From the web demo you can:
- Start a simulated journey and hear/see the rotating helmet/seatbelt safety reminder
- Confirm the safety check and watch your streak/points update
- Submit a citizen incident report (accident, construction, festival, etc.)
- Confirm or dismiss existing reports
- Check the live Green/Yellow/Red safety indicator
- Save a "travel memory" at a location, then check for its "Journey Echo" on return
- View earned badges

### 3. Android client
The `android-client/` folder is a **source scaffold** meant to be dropped
into a real Android Studio project — see `android-client/README.md` for
exactly how to wire it up (FusedLocationProviderClient, TextToSpeech,
Google Maps SDK). It mirrors every feature the backend exposes.

## Why a web demo in a "Java full-stack" project?

The brief calls for a Java backend + a client. The web demo exists so the
**entire system is runnable and demoable in minutes**, without needing
Android Studio, an emulator, or a physical device during a viva. The
Android scaffold shows the intended real client and is ready to build out
further if your submission calls for an actual APK.

## Project scope and honest simplifications

See the "Scope and Simplifications" section of
`docs/Smart_Traffic_Safety_Companion_Abstract.docx` (also in this repo) for
the full list. In short:
- Driving-journey detection uses a GPS speed/distance/duration threshold,
  not full sensor fusion.
- Background tracking runs while the app is active/minimised, not as a
  fully persistent OS-level service.
- Vehicle type (two-wheeler/car) is set by the user, not auto-detected.
- Green/Yellow/Red is derived from citizen report data, not live
  multi-user GPS crowd sensing.
- Report relevance uses a proximity radius, not full route-polyline matching.

These are all called out explicitly so they read as deliberate, defensible
engineering decisions in your report rather than missing features.

## Tech stack
- **Backend:** Java 17, Spring Boot 3.2, Spring Data JPA/Hibernate, H2 (dev) / PostgreSQL (prod)
- **Web demo:** Plain HTML/CSS/JavaScript (fetch API)
- **Android (scaffold):** Java, FusedLocationProviderClient, TextToSpeech, Google Maps SDK

## Next steps to extend this for submission
1. Add Spring Security + JWT if your course requires proper authentication (current login is intentionally simple for an academic build).
2. Flesh out the Android `MainActivity`, map screen, and reporting UI using the scaffold provided.
3. Swap H2 for PostgreSQL for your final deployed/demoed version.
4. Add unit tests for `DrivingDetectionService`, `SafetyColorService`, and `RewardService` — these are the most "interesting" pieces of logic to test and discuss in a viva.
