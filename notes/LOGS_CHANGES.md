# CineMatch – Log of Changes

## 2025-02-20 – Phase 1 Step 1.1: Project Structure Setup

**What:** Implemented Step 1.1 (Project Structure Setup) from APP_DEV_PLAN.

**Changes:**
- **Package structure:** `utils/`, `ui/base/`
- **Constants.java** – App-wide constants: log tags, Firebase node names, TMDB base URL, lobby status values, swiping timeout, room code length. Secrets remain in BuildConfig.
- **Logger.java** – Logging utility wrapping Android Log; filters v/d in release builds.
- **Utils.java** – General helpers: `isBlank`, `isNotBlank`, `orDefault`, `parseInt`.
- **BaseActivity.java** – Base Activity with EdgeToEdge, `applyEdgeToEdgeInsets`, and Logger helpers.
- **BaseFragment.java** – Base Fragment with Logger helpers.

**Files created:**
- `app/src/main/java/com/example/finalprojectandroiddev2/utils/Constants.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/utils/Logger.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/utils/Utils.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/base/BaseActivity.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/base/BaseFragment.java`

**Notes:** Packages `data/`, `model/`, `viewmodel/` will be created when Step 1.2 and later phases add files. Build requires `local.properties` with `sdk.dir` for Android SDK path.

---

## 2025-02-20 – Phase 1 Step 1.2: Data Models

**What:** Implemented Step 1.2 (Data Models) from APP_DEV_PLAN.

**Changes:**
- **Movie** – TMDB API model with @SerializedName for snake_case mapping: id, title, poster_path, backdrop_path, overview, release_date, vote_average, vote_count, popularity, genre_ids, etc.
- **Lobby** – Firebase schema model: roomCode, hostId, createdAt, status, members, movies, matchedMovie. Inner classes: LobbyMember (username, joinedAt, isHost), LobbyMovie (tmdbId, title, posterPath, votes), MatchedMovie (tmdbId, title, posterPath, matchedAt).
- **User** – Firebase Auth user: uid, email, displayName, photoUrl.
- **Vote** – Swiping vote: userId, movieId, liked.

**Files created:**
- `app/src/main/java/com/example/finalprojectandroiddev2/model/Movie.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/model/Lobby.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/model/User.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/model/Vote.java`
