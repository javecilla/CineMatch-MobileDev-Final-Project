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

---

## 2025-02-20 – Phase 2 Step 2.1: Splash Screen

**What:** Implemented Step 2.1 (Splash Screen) from APP_DEV_PLAN.

**Changes:**

- **activity_splash.xml** – ConstraintLayout with dark background, centered logo (app_logo_black_transparent.png), Material3 CircularProgressIndicator at bottom. IDs: container_splash, image_logo, progress_loading.
- **SplashActivity.java** – Extends BaseActivity, sets splash layout, applies edge-to-edge insets. Empty of navigation logic (per plan).
- **Logo** – Copied app_logo_black_transparent.png to drawable (best for dark theme: transparent, vibrant gradients).
- **Colors** – Added dark theme palette: color_background (#050509), color_surface (#171923), color_primary (#39E1C6), etc. (APP_FLOW).
- **Themes** – Switched to Theme.Material3.Dark.NoActionBar, applied dark palette.
- **Manifest** – SplashActivity set as launcher; MainActivity no longer launcher.
- **Strings** – app_name set to "CineMatch".

**Files created/updated:**

- `app/src/main/res/layout/activity_splash.xml` (new)
- `app/src/main/res/drawable/app_logo_black_transparent.png` (copied)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/splash/SplashActivity.java` (new)
- `app/src/main/res/values/colors.xml`, `themes.xml`, `strings.xml` (updated)
- `app/src/main/res/values-night/themes.xml` (updated)
- `app/src/main/AndroidManifest.xml` (updated)

**Fix (navigation):** SplashActivity was not navigating to LoginActivity. Added Handler.postDelayed (2s) to start LoginActivity and finish SplashActivity after the splash is shown.

---

## 2025-02-20 – Phase 2 Step 2.2: Login Screen

**What:** Implemented Step 2.2 (Login Screen) from APP_DEV_PLAN.

**Changes:**

- **activity_login.xml** – ScrollView with ConstraintLayout, Material3 TextInputLayout.OutlinedBox for email and password, error TextView (visibility gone), Sign In MaterialButton, Create account link (btn_register). IDs: container_login, input_email, input_password, edit_email, edit_password, text_error, btn_sign_in, btn_register.
- **LoginActivity.java** – Extends BaseActivity, sets login layout, applies edge-to-edge insets. No navigation or auth logic (per Phase 2: layouts without functionality).
- **Strings** – label_email, label_password, btn_sign_in, btn_create_account.
- **Manifest** – LoginActivity registered with windowSoftInputMode adjustResize.

**Files created/updated:**

- `app/src/main/res/layout/activity_login.xml` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/LoginActivity.java` (new)
- `app/src/main/res/values/strings.xml` (updated)
- `app/src/main/AndroidManifest.xml` (updated)

**Fix (layout):** Changed layout_height from wrap_content to match_parent in activity_login.xml to ensure the layout fills the screen. Also added logo image to the layout.

---

## 2025-02-20 – Phase 2 Step 2.3: Registration Screen

**What:** Implemented Step 2.3 (Registration Screen) from APP_DEV_PLAN.

**Changes:**
- **activity_registration.xml** – ScrollView with ConstraintLayout, logo, Material3 TextInputLayout for email, password, confirm-password, error TextView (visibility gone), Register button, "Already have an account? Sign In" link. Vertically centered layout matching login.
- **RegistrationActivity.java** – Extends BaseActivity, navigates to LoginActivity on "Sign In" click.
- **LoginActivity** – Added navigation to RegistrationActivity on "Create account" click.
- **Strings** – label_confirm_password, btn_register, btn_already_have_account.
- **Manifest** – RegistrationActivity registered with windowSoftInputMode adjustResize.

**Files created/updated:**
- `app/src/main/res/layout/activity_registration.xml` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/RegistrationActivity.java` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/LoginActivity.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)
- `app/src/main/AndroidManifest.xml` (updated)

---

## 2025-02-20 – Phase 2 Step 2.4: Home Screen

**What:** Implemented Step 2.4 (Home Screen) from APP_DEV_PLAN.

**Changes:**
- **activity_home.xml** – MaterialCardView profile section (image_avatar, text_username, text_email, btn_logout), welcome text, Create Lobby and Join Lobby buttons in MaterialCards. IDs: container_home, card_profile, image_avatar, text_username, text_email, btn_logout, btn_create_lobby, btn_join_lobby.
- **HomeActivity.java** – Extends BaseActivity, logout navigates to LoginActivity and clears back stack.
- **LoginActivity** – Sign In button navigates to HomeActivity (placeholder until Firebase Auth).
- **Strings** – title_home, label_avatar, btn_create_lobby, btn_join_lobby, btn_logout, msg_welcome_home.
- **Drawable** – bg_avatar_placeholder.xml (oval shape for avatar).
- **Manifest** – HomeActivity registered.

**Files created/updated:**
- `app/src/main/res/layout/activity_home.xml` (new)
- `app/src/main/res/drawable/bg_avatar_placeholder.xml` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/HomeActivity.java` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/LoginActivity.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)
- `app/src/main/AndroidManifest.xml` (updated)

---

## 2025-02-20 – Phase 2 Step 2.5: Create Lobby Screen

**What:** Implemented Step 2.5 (Create Lobby Dialog/Screen) from APP_DEV_PLAN.

**Changes:**
- **activity_create_lobby.xml** – Room code card (large, copyable text_room_code), Share Room Code button, member list card with "Waiting for members…" text and RecyclerView, Start Swiping button (disabled). Material 3 cards, dark theme.
- **item_member.xml** – RecyclerView item for member list (avatar, username, host badge).
- **MemberAdapter** – Empty adapter for member RecyclerView, ready for Firebase data.
- **CreateLobbyActivity** – Extends BaseActivity, sets placeholder room code "------", configures RecyclerView with MemberAdapter.
- **HomeActivity** – Create Lobby button navigates to CreateLobbyActivity.
- **Strings** – label_room_code, label_members, label_host, btn_share_room_code, btn_start_swiping, msg_waiting_for_members.
- **Manifest** – CreateLobbyActivity registered.

**Files created/updated:**
- `app/src/main/res/layout/activity_create_lobby.xml` (new)
- `app/src/main/res/layout/item_member.xml` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/CreateLobbyActivity.java` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/MemberAdapter.java` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/HomeActivity.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)
- `app/src/main/AndroidManifest.xml` (updated)

---

## 2025-02-20 – Phase 2 Step 2.6: Join Lobby Screen

**What:** Implemented Step 2.6 (Join Lobby Screen) from APP_DEV_PLAN.

**Changes:**
- **activity_join_lobby.xml** – Logo, room code TextInputLayout (6 char, textCapCharacters, counter), error TextView (visibility gone), Join button, Back button. Vertically centered, dark theme.
- **JoinLobbyActivity** – Extends BaseActivity, Back button calls finish().
- **HomeActivity** – Join Lobby button navigates to JoinLobbyActivity.
- **Strings** – btn_join, btn_back, msg_invalid_room_code.
- **Manifest** – JoinLobbyActivity registered with windowSoftInputMode adjustResize.

**Files created/updated:**
- `app/src/main/res/layout/activity_join_lobby.xml` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/JoinLobbyActivity.java` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/HomeActivity.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)
- `app/src/main/AndroidManifest.xml` (updated)

---

## 2025-02-20 – Phase 2 Step 2.7: Lobby Screen

**What:** Implemented Step 2.7 (Lobby Screen) from APP_DEV_PLAN.

**Changes:**
- **activity_lobby.xml** – Room code card at top, Start Swiping button (host only), member list card with RecyclerView, Leave Lobby button, progress indicator (visibility gone).
- **item_member.xml** – Added status indicator (green dot) and text_status for online/voting.
- **MemberAdapter** – Added status field to MemberItem, bind text_status and indicator_status.
- **bg_status_online.xml** – Green circle drawable for online indicator.
- **LobbyActivity** – Receives EXTRA_ROOM_CODE and EXTRA_IS_HOST, shows host-only Start Swiping, configures member RecyclerView, Leave navigates to Home.
- **CreateLobbyActivity** – Start Swiping enabled for testing, navigates to LobbyActivity as host.
- **JoinLobbyActivity** – Join validates 6-char code, navigates to LobbyActivity with room code.
- **Strings** – btn_leave_lobby, status_online, status_voting.
- **Manifest** – LobbyActivity registered.

**Files created/updated:**
- `app/src/main/res/layout/activity_lobby.xml` (new)
- `app/src/main/res/drawable/bg_status_online.xml` (new)
- `app/src/main/res/layout/item_member.xml` (updated)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/LobbyActivity.java` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/MemberAdapter.java` (updated)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/CreateLobbyActivity.java` (updated)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/JoinLobbyActivity.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)
- `app/src/main/AndroidManifest.xml` (updated)
