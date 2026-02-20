---
description: 
alwaysApply: true
---

---
description: 
alwaysApply: true
---

# AI Agent Instructions — CineMatch

This document instructs AI agents working on **CineMatch: Real-time Collaborative Movie Discovery Engine**. Follow these guidelines so your changes fit the project and avoid duplicating existing work.

---

## 📖 Required Reading Before Any Implementation

Before writing or changing code, read these files in order:

1. **`notes/APP_FLOW.md`** — Architecture, data flow, Firebase schema, TMDB integration, UI/UX (including dark theme), project structure, and constraints.
2. **`notes/APP_DEV_PLAN.md`** — Implementation plan with phases, tasks, and deliverables.
3. **`notes/LOGS_CHANGES.md`** — Log of completed and recent work. **Always check this before implementing any feature** to avoid duplication.

---

## ⚠️ Mandatory Workflow

1. **Before implementing a feature:**  
   - Open `notes/LOGS_CHANGES.md` and confirm the feature is not already done or in progress.  
   - If it is, do **not** implement it again.

2. **After implementing or modifying anything:**  
   - Append an entry to `notes/LOGS_CHANGES.md` with:
     - What was done
     - Which files were created/updated
     - Date (and optional: author/agent)

3. **When planning work:**  
   - Use `notes/APP_DEV_PLAN.md` to choose tasks and phases.  
   - Mark completed tasks (e.g. `[x]`) in the plan if appropriate.

---

## 🎯 Project Summary

**CineMatch** is an Android app for collaborative movie discovery. Users swipe through movies, join lobbies with friends, and when everyone swipes “Yes” on the same movie, a match is found.

- **API:** TMDB (The Movie Database)
- **Backend:** Firebase Realtime Database
- **Auth:** Firebase Authentication
- **UI:** Material Design 3, dark theme

**Core flows:**
1. Auth (Registration, Login) → Home
2. Home → Create Lobby / Join Lobby
3. Lobby → Room code, member list, Start Swiping
4. Swiping → Swipe Yes/No on TMDB movies, real-time votes in Firebase
5. Match → All members vote Yes on the same movie → Match screen

---

## 🛠 Technical Constraints

| Constraint | Value | Notes |
|------------|-------|-------|
| **Language** | Java 11 | Do **not** add Kotlin or convert to Kotlin |
| **Build** | Gradle (Kotlin DSL) | `build.gradle.kts`, `app/build.gradle.kts` |
| **Namespace** | `com.example.finalprojectandroiddev2` | Use for all Java classes |
| **Min SDK** | 26 | Android 8.0+ |
| **Target SDK** | 36 | Latest supported |
| **Architecture** | MVVM (preferred) or MVP | See `notes/APP_FLOW.md` |

**Dependencies (already present):**
- Retrofit 2, Gson, OkHttp
- Firebase Auth, Firebase Realtime Database
- Material, AppCompat, ConstraintLayout

**Credentials / config:**
- TMDB token and API key: in `local.properties`, exposed via `BuildConfig`
- Firebase Realtime DB URL: in `local.properties`, `BuildConfig.FB_ROUTE_INSTANCE_URL`

---

## 📁 Project Structure

Code lives under:

```
app/src/main/java/com/example/finalprojectandroiddev2/
├── ui/           # Activities, Fragments
│   ├── auth/
│   ├── home/
│   ├── lobby/
│   ├── swiping/
│   └── match/
├── data/
│   ├── api/      # Retrofit interfaces, API client
│   ├── repository/
│   └── model/
├── viewmodel/
└── utils/
```

- **Layouts:** `app/src/main/res/layout/`
- **Values:** `app/src/main/res/values/` (strings, colors, themes)
- **Manifest:** `app/src/main/AndroidManifest.xml`

Use the layout and class naming patterns from `notes/APP_DEV_PLAN.md` (e.g. `activity_auth.xml`, `AuthActivity.java`).

---

## 🎨 UI/UX Rules

1. **Theme:** Dark theme only. Use the palette from `notes/APP_FLOW.md`:
   - Background: near-black / charcoal
   - Surface / cards: dark slate
   - Primary accent: teal / cyan
   - Secondary accent: amber / yellow for ratings
   - Text: white primary, gray secondary

2. **Components:** Use Material 3 components (Card, Button, TextInputLayout, etc.).

3. **Screens:** Follow the screen list in `notes/APP_FLOW.md` (Splash, Auth, Home, Lobby, Swiping, Match).

---

## 📋 Course Requirements (IT 308W)

These must be satisfied:

- **Activities:** Registration, Login, and Main/primary screens.
- **Firebase Auth:** Create accounts and log in via Firebase.
- **Free API:** TMDB is used (no OpenWeather or weather APIs).
- **Firebase Realtime Database:** Required for collaborative features (e.g. lobby, votes).
- **Feature quality:** The main features must be more substantial than simple “add to cart” or “add to favorites” only.

CineMatch satisfies these with: lobby/room system, real-time collaborative swiping, and match detection, all backed by Firebase Realtime Database.

---

## ✅ Do's

- Check `notes/LOGS_CHANGES.md` before implementing features.
- Update `notes/LOGS_CHANGES.md` after every meaningful change.
- Use the Firebase schema from `notes/APP_FLOW.md` for lobby, members, votes, and matched movie.
- Use TMDB endpoints as documented.
- Keep API keys and tokens in `local.properties` and `BuildConfig`; never commit them.
- Use Java, Retrofit, Gson, Glide/Picasso for images.
- Keep package structure: `ui`, `data`, `model`, `viewmodel`, `utils`.
- Follow the dark theme color palette from `notes/APP_FLOW.md`.

---

## ❌ Don'ts

- Do **not** add Kotlin or convert existing Java to Kotlin.
- Do **not** implement features that are already recorded as done in `notes/LOGS_CHANGES.md`.
- Do **not** hardcode API keys, tokens, or Firebase URLs.
- Do **not** change the Firebase schema without updating `notes/APP_FLOW.md`.
- Do **not** use OpenWeather or weather-related APIs.
- Do **not** rely solely on “add to cart” or “add to favorites” as the main features.

---

## 🔗 Quick Reference

| Document | Purpose |
|----------|---------|
| `notes/APP_FLOW.md` | Architecture, flows, schema, UI, constraints |
| `notes/APP_DEV_PLAN.md` | Phases and implementation tasks |
| `notes/LOGS_CHANGES.md` | Completed and recent work (single source of truth) |
| `notes/INSTRUCTIONS.txt` | Course instructions |
| `README.md` | Project overview for humans |

---

## 📝 Summary for Agents

1. Read `APP_FLOW.md`, `APP_DEV_PLAN.md`, and `LOGS_CHANGES.md` before coding.
2. Always consult `LOGS_CHANGES.md` to avoid duplicating work.
3. Use Java 11; follow MVVM/MVP and the project structure above.
4. Stick to the dark theme and Material 3.
5. After any implementation or modification, update `LOGS_CHANGES.md`.

---

*This file is the primary instruction set for AI agents working on CineMatch. Keep it updated as project rules change.*
