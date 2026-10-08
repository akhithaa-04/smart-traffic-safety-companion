# API Reference

Base URL: `http://localhost:8080/api`

All requests/responses are JSON. Errors come back as `{ "error": "message" }`
with an appropriate HTTP status (400 bad request, 404 not found, 409 conflict).

---

## Auth

### `POST /auth/register`
```json
{ "name": "Asha Rao", "email": "asha@example.com", "password": "password123", "vehicleType": "TWO_WHEELER" }
```
`vehicleType` is one of `TWO_WHEELER`, `CAR`, `BOTH` (optional, defaults to `BOTH`).
Returns the created `User`.

### `POST /auth/login`
```json
{ "email": "asha@example.com", "password": "password123" }
```
Returns the `User`.

---

## Users

### `GET /users/{id}`
Returns the `User` (id, name, email, vehicleType, points, safeStartStreak).

---

## Journeys

### `POST /journeys/detect`
Client calls this periodically with recent GPS samples summarised, to check
if the pattern looks like a genuine driving journey.
```json
{ "averageSpeedKmh": 22.5, "totalDistanceMeters": 400, "durationSeconds": 60 }
```
Returns `{ "isDrivingJourney": true }`.

### `POST /journeys/start`
Call once `detect` returns true. Creates the Journey and returns the
rotating, personalised safety reminder for the client to speak/display.
```json
{
  "userId": 1,
  "latitude": 17.4475,
  "longitude": 78.3563,
  "vehicleTypeUsed": "TWO_WHEELER",
  "raining": false,
  "nightTime": false
}
```
Response:
```json
{ "journey": { "id": 5, "status": "ACTIVE", "...": "..." }, "safetyReminder": "Helmet on? Let's roll safely." }
```

### `POST /journeys/{id}/confirm-safety-check`
Call when the user taps/says "Got it" on the reminder. Increments their
safe-start streak and awards points.

### `POST /journeys/{id}/end`
```json
{ "latitude": 17.46, "longitude": 78.40 }
```
Marks the journey `COMPLETED`. If the safety check was never confirmed,
resets the user's streak.

### `GET /journeys/user/{userId}`
Journey history for a user, most recent first.

---

## Incident Reports (citizen reporting)

### `POST /reports`
Submit a report — works whether or not the citizen is on an active journey.
```json
{
  "reporterId": 1,
  "type": "FESTIVAL_OR_PROCESSION",
  "latitude": 17.385,
  "longitude": 78.4867,
  "description": "Procession blocking the main road",
  "cause": "Ganesh Chaturthi",
  "photoUrl": "https://...",
  "estimatedDurationMinutes": 180,
  "suggestedAlternateRoute": "Use the Koti flyover"
}
```
`type` is one of `ACCIDENT`, `ROAD_CONSTRUCTION`, `WATERLOGGING`,
`FESTIVAL_OR_PROCESSION`, `FALLEN_TREE`, `ROAD_BLOCKAGE`,
`HEAVY_CONGESTION`, `OTHER`.

Awards the reporter points (+bonus if a photo is included).

### `GET /reports`
All currently `ACTIVE` reports (auto-expires stale ones first).

### `GET /reports/nearby?lat=&lng=&radiusMeters=1000`
Active reports within radius of a point — this is the "relevant to this
journey" check the client runs while a journey is active.

### `GET /reports/safety-color?lat=&lng=&radiusMeters=1000`
```json
{ "color": "YELLOW" }
```
`GREEN`, `YELLOW`, or `RED`, derived from nearby reports' freshness and net confirmations.

### `POST /reports/{id}/respond`
```json
{ "userId": 2, "actionType": "CONFIRM" }
```
`actionType` is `CONFIRM` or `DISMISS`. A user can only respond once per
report (second attempt returns 409 Conflict). Awards points either way.
Enough net dismissals auto-marks a report `CLEARED`.

---

## Badges

### `GET /badges`
All badges defined in the system.

### `GET /badges/user/{userId}`
Badges a specific user has earned.

---

## Travel Memory ("Journey Echo")

### `POST /memories`
```json
{ "userId": 1, "latitude": 17.4475, "longitude": 78.3563, "placeName": "Golconda Fort", "note": "Watched the sunset" }
```

### `GET /memories/user/{userId}`
A user's saved memories, most recent first.

### `GET /memories/echo?userId=1&lat=17.4475&lng=78.3563`
Checks whether the current location matches a past memory within the
geofence radius. Returns `{ "echo": "You were at Golconda Fort yesterday. Watched the sunset." }`
or `{}` if nothing nearby.
