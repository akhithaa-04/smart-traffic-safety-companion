# Database Schema

Managed by Hibernate (`spring.jpa.hibernate.ddl-auto=update`) from the
entity classes in `backend/src/main/java/com/stsc/backend/model/`. Tables
below reflect what Hibernate generates from those entities.

## app_user
| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK, identity | |
| email | VARCHAR, unique, not null | |
| name | VARCHAR, not null | |
| password | VARCHAR, not null | plain text in this academic build - hash with BCrypt for production |
| vehicle_type | VARCHAR (enum) | TWO_WHEELER / CAR / BOTH |
| points | INT | reward total |
| safe_start_streak | INT | consecutive confirmed pre-journey safety checks |
| created_at | TIMESTAMP | |

## journey
| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK | |
| user_id | BIGINT, FK -> app_user | |
| start_lat / start_lng | DOUBLE | |
| end_lat / end_lng | DOUBLE, nullable | set when journey ends |
| started_at / ended_at | TIMESTAMP | |
| status | VARCHAR (enum) | ACTIVE / COMPLETED |
| vehicle_type_used | VARCHAR (enum) | vehicle used for this specific trip |
| safety_check_confirmed | BOOLEAN | whether the helmet/seatbelt reminder was confirmed |

## incident_report
| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK | |
| reporter_id | BIGINT, FK -> app_user | |
| type | VARCHAR (enum) | ACCIDENT / ROAD_CONSTRUCTION / WATERLOGGING / FESTIVAL_OR_PROCESSION / FALLEN_TREE / ROAD_BLOCKAGE / HEAVY_CONGESTION / OTHER |
| latitude / longitude | DOUBLE | |
| description | VARCHAR(1000) | |
| cause | VARCHAR(500) | |
| photo_url | VARCHAR | |
| estimated_duration_minutes | INT, nullable | |
| suggested_alternate_route | VARCHAR(500) | |
| confirm_count / dismiss_count | INT | denormalised counters, kept in sync with report_confirmation |
| status | VARCHAR (enum) | ACTIVE / EXPIRED / CLEARED |
| created_at | TIMESTAMP | |
| last_confirmed_at | TIMESTAMP, nullable | |

## report_confirmation
| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK | |
| report_id | BIGINT, FK -> incident_report | |
| user_id | BIGINT, FK -> app_user | |
| action_type | VARCHAR (enum) | CONFIRM / DISMISS |
| created_at | TIMESTAMP | |

Unique constraint on `(report_id, user_id)` — one response per user per report.

## badge
| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK | |
| name | VARCHAR, unique | |
| description | VARCHAR(500) | |
| criteria_type | VARCHAR (enum) | POINTS / SAFE_START_STREAK / CONFIRMED_REPORT_COUNT |
| threshold | INT | value the criteria must reach |

## user_badge
| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK | |
| user_id | BIGINT, FK -> app_user | |
| badge_id | BIGINT, FK -> badge | |
| earned_at | TIMESTAMP | |

Unique constraint on `(user_id, badge_id)` — each badge earned once per user.

## travel_memory
| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK | |
| user_id | BIGINT, FK -> app_user | |
| latitude / longitude | DOUBLE | |
| place_name | VARCHAR, nullable | |
| note | VARCHAR(1000), nullable | |
| photo_url | VARCHAR, nullable | |
| visited_at | TIMESTAMP | when the memory was first saved |
| last_resurfaced_at | TIMESTAMP, nullable | last time the "Journey Echo" fired for this memory |
| times_revisited | INT | how many times the echo has fired |

---

## Entity Relationship summary

```
app_user 1---* journey
app_user 1---* incident_report   (as reporter)
app_user 1---* report_confirmation
app_user 1---* user_badge
app_user 1---* travel_memory

incident_report 1---* report_confirmation
badge 1---* user_badge
```

## Why these denormalised counters?

`incident_report.confirm_count` / `dismiss_count` are kept as simple
integers on the report itself (rather than always computing `COUNT(*)` over
`report_confirmation`) so the frequently-read endpoints (`/reports`,
`/reports/nearby`, `/reports/safety-color`) stay cheap. The
`report_confirmation` table still exists as the source of truth and audit
trail, and to enforce "one response per user per report."
