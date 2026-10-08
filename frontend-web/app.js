const API_BASE = "http://localhost:8080/api";

// Demo coordinates near Jubilee Hills, Hyderabad - matches seeded report data
const DEMO_LAT = 17.4475;
const DEMO_LNG = 78.3563;

let currentUser = null;
let activeJourneyId = null;

async function api(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.error || "Request failed");
  return data;
}

// ---------- Auth ----------
async function login() {
  const email = document.getElementById("login-email").value;
  const password = document.getElementById("login-password").value;
  try {
    currentUser = await api("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
    renderUser();
  } catch (e) {
    alert(e.message);
  }
}

function logout() {
  currentUser = null;
  activeJourneyId = null;
  document.getElementById("logged-out-view").classList.remove("hidden");
  document.getElementById("logged-in-view").classList.add("hidden");
}

function renderUser() {
  document.getElementById("logged-out-view").classList.add("hidden");
  document.getElementById("logged-in-view").classList.remove("hidden");
  document.getElementById("current-user-name").textContent = currentUser.name;
  document.getElementById("current-user-points").textContent = currentUser.points;
  document.getElementById("current-user-streak").textContent = currentUser.safeStartStreak;
  document.getElementById("current-user-vehicle").textContent = currentUser.vehicleType;
}

function requireLogin() {
  if (!currentUser) {
    alert("Please log in first (use one of the demo accounts).");
    return false;
  }
  return true;
}

// ---------- Journey ----------
async function startJourney() {
  if (!requireLogin()) return;
  const raining = document.getElementById("raining").checked;
  const night = document.getElementById("night").checked;

  // Step 1: simulate the driving-detection check the client would do continuously
  const detection = await api("/journeys/detect", {
    method: "POST",
    body: JSON.stringify({ averageSpeedKmh: 22, totalDistanceMeters: 400, durationSeconds: 60 }),
  });

  if (!detection.isDrivingJourney) {
    alert("GPS pattern doesn't look like a driving journey yet.");
    return;
  }

  // Step 2: start the journey & get the personalised safety reminder
  const result = await api("/journeys/start", {
    method: "POST",
    body: JSON.stringify({
      userId: currentUser.id,
      latitude: DEMO_LAT,
      longitude: DEMO_LNG,
      raining,
      nightTime: night,
    }),
  });

  activeJourneyId = result.journey.id;
  const box = document.getElementById("reminder-box");
  box.textContent = "🔊 " + result.safetyReminder;
  box.classList.remove("hidden");
  document.getElementById("active-journey-box").classList.remove("hidden");
}

async function confirmSafetyCheck() {
  if (!activeJourneyId) return;
  await api(`/journeys/${activeJourneyId}/confirm-safety-check`, { method: "POST" });
  alert("Safety check confirmed! Streak updated.");
  currentUser = await api(`/users/${currentUser.id}`);
  renderUser();
}

async function endJourney() {
  if (!activeJourneyId) return;
  await api(`/journeys/${activeJourneyId}/end`, {
    method: "POST",
    body: JSON.stringify({ latitude: DEMO_LAT + 0.01, longitude: DEMO_LNG + 0.01 }),
  });
  activeJourneyId = null;
  document.getElementById("reminder-box").classList.add("hidden");
  document.getElementById("active-journey-box").classList.add("hidden");
  alert("Journey ended.");
}

// ---------- Safety color ----------
async function checkSafetyColor() {
  const result = await api(`/reports/safety-color?lat=${DEMO_LAT}&lng=${DEMO_LNG}&radiusMeters=1000`);
  const el = document.getElementById("safety-color-box");
  el.innerHTML = `<span class="color-pill color-${result.color}">${result.color}</span>`;
}

// ---------- Reports ----------
async function submitReport() {
  if (!requireLogin()) return;
  const payload = {
    reporterId: currentUser.id,
    type: document.getElementById("report-type").value,
    latitude: DEMO_LAT + (Math.random() - 0.5) * 0.01,
    longitude: DEMO_LNG + (Math.random() - 0.5) * 0.01,
    description: document.getElementById("report-desc").value,
    cause: document.getElementById("report-cause").value,
    estimatedDurationMinutes: Number(document.getElementById("report-duration").value) || null,
    suggestedAlternateRoute: document.getElementById("report-alt-route").value,
  };
  await api("/reports", { method: "POST", body: JSON.stringify(payload) });
  alert("Report submitted! Points awarded.");
  currentUser = await api(`/users/${currentUser.id}`);
  renderUser();
  loadReports();
}

async function loadReports() {
  const reports = await api("/reports");
  const el = document.getElementById("reports-list");
  el.innerHTML = reports.length ? "" : "<p class='card-desc'>No active reports.</p>";
  reports.forEach((r) => {
    const div = document.createElement("div");
    div.className = "report-item";
    div.innerHTML = `
      <div class="report-type">${r.type.replaceAll("_", " ")}</div>
      <div>${r.description || ""}</div>
      <span class="meta">${r.cause || "no cause given"} &middot; est. ${r.estimatedDurationMinutes || "?"} min &middot;
      ${r.confirmCount} confirmed / ${r.dismissCount} cleared</span>
      <div class="report-actions">
        <button class="btn-ghost btn-sm" onclick="respond(${r.id}, 'CONFIRM')">Still happening</button>
        <button class="btn-ghost btn-sm" onclick="respond(${r.id}, 'DISMISS')">Mark cleared</button>
      </div>
    `;
    el.appendChild(div);
  });
}

async function respond(reportId, actionType) {
  if (!requireLogin()) return;
  try {
    await api(`/reports/${reportId}/respond`, {
      method: "POST",
      body: JSON.stringify({ userId: currentUser.id, actionType }),
    });
    currentUser = await api(`/users/${currentUser.id}`);
    renderUser();
    loadReports();
  } catch (e) {
    alert(e.message);
  }
}

// ---------- Badges ----------
async function loadBadges() {
  if (!requireLogin()) return;
  const badges = await api(`/badges/user/${currentUser.id}`);
  const el = document.getElementById("badges-list");
  el.innerHTML = badges.length
    ? badges.map((b) => `<span class="badge-chip">${b.badge.name}</span>`).join("")
    : "<p class='card-desc'>No badges yet, submit reports or confirm a safety check streak to earn some.</p>";
}

// ---------- Travel memory ----------
async function saveMemory() {
  if (!requireLogin()) return;
  await api("/memories", {
    method: "POST",
    body: JSON.stringify({
      userId: currentUser.id,
      latitude: DEMO_LAT,
      longitude: DEMO_LNG,
      placeName: document.getElementById("memory-place").value,
      note: document.getElementById("memory-note").value,
    }),
  });
  alert("Memory saved at this location.");
}

async function checkEcho() {
  if (!requireLogin()) return;
  const result = await api(`/memories/echo?userId=${currentUser.id}&lat=${DEMO_LAT}&lng=${DEMO_LNG}`);
  const el = document.getElementById("echo-box");
  el.innerHTML = result.echo
    ? `<div class="echo-box">💭 ${result.echo}</div>`
    : "<p class='card-desc'>No memory to resurface here yet.</p>";
}

// Initial load
loadReports();
