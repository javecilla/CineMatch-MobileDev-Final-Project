## CineMatch QUICK START Guide

This guide helps developers and AI agents get **CineMatch** running quickly on a new machine.

---

## 🧩 1. Prerequisites

- **Android Studio** (latest stable, with Android SDK up to API 36 installed)
- **Java 11** (via Android Studio / Gradle toolchain)
- Stable internet connection (for TMDB + Firebase)
- A **Firebase project** and **TMDB account** (free tiers)

---

## 📦 2. Clone & Open the Project

1. Clone the repository or open the existing folder in Android Studio:
   - `d:\AndroidStudioProjects\FinalProjectAndroidDev2` (or your chosen path).
2. Let Android Studio **index and sync Gradle**.  
   - If prompted to trust the Gradle project, accept.

---

## 🔐 3. Configure `local.properties`

The project relies on `local.properties` for **secrets and configuration**.  
These values are injected into `BuildConfig` and **must not** be committed.

1. In the project root, create (or edit) `local.properties`.
2. Use `local.properties.example` as a reference.
3. Add your values:

```properties
sdk.dir=PATH_TO_ANDROID_SDK

# The Movie Database (TMDB)
TMDB_READ_ACCESS_TOKEN=YOUR_TMDB_BEARER_TOKEN
TMDB_API_KEY=YOUR_TMDB_API_KEY

# Firebase
FB_PROJECT_NAME=your-project-name
FB_PROJECT_ID=your-project-id
FB_PROJECT_NUMBER=your-project-number
FB_APP_ID=your-app-id
FB_PACKAGE_NAME=com.example.finalprojectandroiddev2
FB_ROUTE_INSTANCE_URL=https://your-project-id-region.firebasedatabase.app
```

4. Make sure `local.properties` is **ignored by Git** (already handled by `.gitignore`).

---

## 🔥 4. Firebase Console Setup (High Level)

1. Go to the **Firebase Console** and create a project (if not already created).
2. Add an **Android app** with:
   - Package name: `com.example.finalprojectandroiddev2`
   - Download `google-services.json` and place it under:
     - `app/google-services.json`
3. Enable:
   - **Authentication** → Email/Password (and others if needed).
   - **Realtime Database** → Start in test mode (for development), then tighten rules later.
4. Copy your Realtime Database URL into `FB_ROUTE_INSTANCE_URL` in `local.properties`.

> For detailed behavior and schema, see `APP_FLOW.md` (Firebase section).

---

## 🎬 5. TMDB Setup (High Level)

1. Create a free account on TMDB and generate:
   - **API Key**
   - **Read Access Token (v4)** (used as bearer token)
2. Put both into `local.properties` as shown above.
3. The app will use `BuildConfig.TMDB_READ_ACCESS_TOKEN` and `BuildConfig.TMDB_API_KEY` automatically; you do not need to hardcode anything.

---

## ▶️ 6. Run the App

1. Connect an **Android device** or start an **emulator** (API 26+ recommended).
2. In Android Studio, select the `app` configuration.
3. Click **Run** (▶).
4. On first launch you should:
   - See the default `MainActivity` / `activity_main.xml` UI (before full implementation), or
   - Once implemented, go through: Splash → Auth → Home → Lobby / Swiping.

If the app crashes or fails to build, check:
- Gradle sync output
- Logcat for runtime errors
- `local.properties` for missing keys

---

## 🧭 7. Where to Start Coding

- **High-level architecture & flows**: `notes/APP_FLOW.md`
- **Implementation phases & tasks**: `notes/APP_DEV_PLAN.md`
- **Agent rules & constraints**: `notes/AGENTS.md`
- **Coding and naming rules**: `notes/CONVENTIONS.md`

Suggested order for new contributors:

1. Read `AGENTS.md`, `APP_FLOW.md`, `APP_DEV_PLAN.md`, and `CONVENTIONS.md`.
2. Pick a **Phase 1 or Phase 2** task from `APP_DEV_PLAN.md`.
3. Check `LOGS_CHANGES.md` to avoid duplicating work.
4. Implement the task following conventions.
5. Log your change in `LOGS_CHANGES.md`.

---

## 🛠 8. Common Issues (Quick Hints)

- **Gradle sync fails**  
  - Verify `sdk.dir` in `local.properties`.  
  - Ensure internet access for downloading dependencies.

- **Firebase connection errors**  
  - Check `FB_ROUTE_INSTANCE_URL` and ensure it matches your Realtime Database URL.  
  - Confirm `google-services.json` is in the `app` module.

- **TMDB 401 / auth errors**  
  - Confirm `TMDB_READ_ACCESS_TOKEN` is a valid v4 read access token.  
  - Check for extra quotes / spaces in `local.properties`.

For deeper troubleshooting and patterns, see (or create) `notes/TROUBLESHOOTING.md`.

---

## ✅ Quick Checklist for a Fresh Setup

- [ ] Android Studio installed (with API 26+ SDKs).  
- [ ] Project opened and Gradle synced without errors.  
- [ ] `local.properties` configured with SDK path, TMDB, and Firebase values.  
- [ ] `google-services.json` placed in the `app/` module.  
- [ ] App builds and runs on emulator/device.  
- [ ] Read `AGENTS.md`, `APP_FLOW.md`, `APP_DEV_PLAN.md`, `CONVENTIONS.md`.  
- [ ] Ready to pick a task and log changes in `LOGS_CHANGES.md`.

