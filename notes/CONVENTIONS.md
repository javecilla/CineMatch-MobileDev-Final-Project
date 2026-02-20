## CineMatch Code & Project Conventions

This document defines **coding, naming, and structure conventions** for the CineMatch project so all developers and AI agents can work consistently.

---

## 🔤 Language & General Style

- **Primary language**: **Java 11** (no Kotlin).
- **Architecture**: Prefer **MVVM**; MVP is acceptable for simpler screens but keep it consistent within a feature.
- **Formatting**:
  - Use the default Android Studio code style for Java and XML.
  - 4 spaces per indent; no tabs.
  - One top-level public class per file.
- **Comments**:
  - Use comments only for intent, non-obvious logic, and trade-offs.
  - Do **not** narrate obvious steps (e.g., `// set text`).

---

## 📦 Packages & Class Naming

- Base package: `com.example.finalprojectandroiddev2`.
- High-level package structure (under `java/com/example/finalprojectandroiddev2`):
  - `ui` – Activities, fragments, adapters, view-specific helpers.
    - `ui.auth`
    - `ui.home`
    - `ui.lobby`
    - `ui.swiping`
    - `ui.match`
  - `data` – Data access and integration.
    - `data.api` – Retrofit interfaces and API client.
    - `data.repository` – Repositories that combine API + Firebase + cache.
    - `data.model` – Plain data models (POJOs).
  - `viewmodel` – ViewModel classes.
  - `utils` – Shared utilities (logging, room code generator, match detection, etc.).

### Class naming rules

- **Activities**: `FeatureActivity`  
  - Examples: `AuthActivity`, `HomeActivity`, `LobbyActivity`, `SwipingActivity`, `MatchActivity`.
- **ViewModels**: `FeatureViewModel`  
  - Example: `LobbyViewModel`, `SwipingViewModel`.
- **Adapters**: `ThingAdapter`  
  - Example: `MemberAdapter`, `MovieCardAdapter`.
- **Repositories**: `ThingRepository`  
  - Example: `AuthRepository`, `FirebaseRepository`, `MovieRepository`.
- **Data models**: Singular nouns, PascalCase.  
  - Example: `Movie`, `Lobby`, `User`, `Vote`, `MovieResponse`.
- **Utilities / helpers**: Descriptive, PascalCase.  
  - Example: `RoomCodeGenerator`, `ImageLoader`, `MatchDetector`, `Logger`, `Constants`.

---

## 🧱 XML Layout & Resource Naming

### Layout files (`res/layout`)

- **Activities**: `activity_<feature>.xml`
  - `activity_auth.xml`, `activity_home.xml`, `activity_lobby.xml`, `activity_swiping.xml`, `activity_match.xml`, `activity_splash.xml`.
- **RecyclerView items / cards**: `item_<what>.xml`
  - `item_member.xml`, `item_movie_card.xml`.
- **Dialogs / bottom sheets**: `dialog_<feature>.xml`, `bottom_<feature>.xml`.
- **State layouts**: `state_<type>.xml`
  - `state_error.xml`, `state_empty.xml`, `state_loading.xml` (if used).

### View IDs

- Use **snake_case** and include type/context:
  - Containers: `container_...` or `layout_...` (e.g., `container_lobby_members`).
  - Buttons: `btn_...` (e.g., `btn_create_lobby`, `btn_join_lobby`, `btn_start_swiping`).
  - TextViews: `text_...` (e.g., `text_room_code`, `text_movie_title`, `text_match_status`).
  - EditTexts/TextInputLayouts: `input_...` (e.g., `input_email`, `input_password`, `input_room_code`).
  - RecyclerViews: `list_...` or `recycler_...` (e.g., `recycler_members`, `recycler_trending_movies`).
  - ImageViews: `image_...` (e.g., `image_poster`, `image_avatar`).

### Strings (`res/values/strings.xml`)

- Use **lower_snake_case**:
  - Screen titles: `title_auth`, `title_home`, `title_lobby`.
  - Buttons: `btn_create_lobby`, `btn_join_lobby`, `btn_start_swiping`.
  - Messages: `msg_invalid_room_code`, `msg_network_error`, `msg_match_found`.
  - Generic: `app_name`, `label_email`, `label_password`.

### Colors & themes

- Keep all color definitions in `colors.xml` or Material 3 theme files.
- Name colors by role, not exact color:
  - `color_background`, `color_surface`, `color_primary`, `color_primary_variant`, `color_secondary`, `color_error`, `color_text_primary`, `color_text_secondary`, `color_nav_background`.
- Colors should match the dark theme palette described in `APP_FLOW.md`.

---

## 🌐 Networking & API Conventions

- All HTTP calls should go through **Retrofit** interfaces in `data.api`.
- Use a single `ApiClient` (or similar) to configure:
  - Base URL: TMDB.
  - OkHttp client with:
    - Interceptor that adds the `Authorization: Bearer <token>` header from `BuildConfig.TMDB_READ_ACCESS_TOKEN`.
- Do **not** hardcode tokens or URLs; always use `BuildConfig`.
- Map TMDB responses into:
  - `Movie` and related model classes in `data.model`.
  - Response wrappers like `MovieListResponse` where needed.

---

## 🔥 Firebase & Realtime Database Conventions

- All Firebase Realtime Database access goes through repository classes in `data.repository`.
- Use the schema documented in `APP_FLOW.md` (`lobbies`, `members`, `movies`, `votes`, `matchedMovie`).
- Keep node names **lower_snake_case** or lowercase (e.g., `lobbies`, `members`, `movies`, `votes`).
- Use **listeners** (`ValueEventListener`, `ChildEventListener`) in repositories or ViewModels, not directly inside Activities when possible.
- Always remove listeners in `onCleared()` (ViewModel) or relevant lifecycle callbacks (e.g., `onStop()` in Activity) to avoid leaks.

---

## 👁 MVVM Responsibilities

- **Activity / Fragment (View)**:
  - Handles UI elements, animations, navigation.
  - Observes LiveData/StateFlow from ViewModel.
  - Does **not** contain business logic or direct Firebase/TMDB calls.
- **ViewModel**:
  - Holds UI state and exposes it to the View.
  - Calls repositories to fetch/update data.
  - Handles simple mapping / combination of data.
- **Repository**:
  - Talks to TMDB (Retrofit), Firebase, and local caches if any.
  - Provides clean methods like `getLobby(roomCode)`, `sendVote(...)`, `fetchPopularMovies()`.

---

## 🧪 Testing & Error Handling Conventions

- For any new non-trivial logic (e.g., `RoomCodeGenerator`, `MatchDetector`):
  - Add unit tests under `app/src/test/java/...`.
- Error handling:
  - Centralize common error mapping where possible (e.g., in repositories).
  - Surface user-friendly messages to the UI (via ViewModel state).
  - Avoid swallowing exceptions silently; log with a utility (`Logger`).

---

## 🧾 Git & Logging Conventions (High-Level)

- When you finish a meaningful piece of work:
  - Update `notes/LOGS_CHANGES.md` (what changed, where, when).
- Commit messages (for human developers):
  - Use short, imperative titles: `Add lobby member list`, `Implement TMDB movie repository`.

---

## 🧭 How Agents Should Use This File

- When creating **new files**, follow the naming patterns here for:
  - Packages
  - Activities, ViewModels, Repositories, Adapters, Utilities
  - XML layout names and view IDs
- When **editing existing code**, align any new elements with these conventions to keep the project consistent.

