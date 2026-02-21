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
- **activity_lobby.xml** – Room code card at top, member list RecyclerView in card, LinearProgressIndicator (visibility gone), Start Swiping button (visibility gone, host only), Leave Lobby outlined button. Dark theme, Material 3.
- **item_member.xml** – Added status indicator View (view_status_indicator) with bg_status_online drawable for online/voting status.
- **bg_status_online.xml** – Circular drawable for member status indicator (primary color).
- **MemberAdapter** – Added viewStatusIndicator field to ViewHolder, added isOnline field to MemberItem, bind status indicator visibility.
- **LobbyActivity** – Extends BaseActivity, configures RecyclerView with MemberAdapter, Leave Lobby navigates to HomeActivity. Start Swiping visibility logic pending Firebase.
- **Strings** – btn_leave_lobby.
- **Manifest** – LobbyActivity registered.

**Files created/updated:**
- `app/src/main/res/layout/activity_lobby.xml` (new)
- `app/src/main/res/drawable/bg_status_online.xml` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/LobbyActivity.java` (new)
- `app/src/main/res/layout/item_member.xml` (updated)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/MemberAdapter.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)
- `app/src/main/AndroidManifest.xml` (updated)

---

## 2025-02-20 – Phase 2 Step 2.8: Swiping Screen

**What:** Implemented Step 2.8 (Swiping Screen) from APP_DEV_PLAN.

**Changes:**
- **activity_swiping.xml** – Member status card (HorizontalScrollView with "Swiping:" label), ViewPager2 for movie cards, Yes/No buttons (outlined No, filled Yes), Exit Session text button. Dark theme, Material 3.
- **item_movie_card.xml** – MaterialCardView with poster ImageView (2:3 ratio), gradient overlay, title, rating (⭐), overview. Card elevation, rounded corners.
- **bg_poster_gradient.xml** – Gradient overlay drawable for poster (transparent to dark).
- **SwipingActivity** – Extends BaseActivity, configures ViewPager2 (adapter pending TMDB), Exit Session navigates to LobbyActivity. Yes/No button handlers pending swipe logic.
- **Strings** – label_movie_poster, label_swiping_status, btn_yes, btn_no, btn_exit_session.
- **build.gradle** – Added ViewPager2 dependency.
- **Manifest** – SwipingActivity registered.

**Files created/updated:**
- `app/src/main/res/layout/activity_swiping.xml` (new)
- `app/src/main/res/layout/item_movie_card.xml` (new)
- `app/src/main/res/drawable/bg_poster_gradient.xml` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/swiping/SwipingActivity.java` (new)
- `app/src/main/res/values/strings.xml` (updated)
- `app/src/main/build.gradle.kts` (updated)
- `app/src/main/AndroidManifest.xml` (updated)

---

## 2025-02-20 – Phase 2 Step 2.9: Match Found Screen

**What:** Implemented Step 2.9 (Match Found Screen) from APP_DEV_PLAN - **Final step of Phase 2**.

**Changes:**
- **activity_match.xml** – ScrollView with celebration text ("🎉 Match Found! 🎉"), large poster ImageView (2:3 ratio), movie details card (title, rating, release date, overview), Watch Now button (primary), Find Another Match button (outlined), Leave Lobby button (text). Dark theme, Material 3.
- **MatchActivity** – Extends BaseActivity, Watch Now placeholder (external link pending), Find Another Match navigates to SwipingActivity, Leave Lobby navigates to HomeActivity and clears back stack. Movie data binding pending Firebase.
- **Strings** – msg_match_found, btn_watch_now, btn_find_another_match.
- **Manifest** – MatchActivity registered.

**Files created/updated:**
- `app/src/main/res/layout/activity_match.xml` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/match/MatchActivity.java` (new)
- `app/src/main/res/values/strings.xml` (updated)
- `app/src/main/AndroidManifest.xml` (updated)

**Phase 2 Complete:** All UI layouts and screens are now implemented. Ready for Phase 3: Functionality & Integration.

---

## 2025-02-20 – Phase 3 Step 3.1: Firebase Auth Setup

**What:** Implemented Step 3.1 (Firebase Auth Setup) from APP_DEV_PLAN.

**Changes:**

- **CineMatchApplication** – Application class that initializes Firebase Auth on app startup. Firebase Auth is automatically initialized via google-services.json, but we verify it's ready.
- **AuthRepository** – Singleton repository for Firebase Authentication operations:
  - `signIn(email, password, callback)` – Signs in user with email/password, handles Firebase errors and converts to user-friendly messages
  - `signUp(email, password, callback)` – Creates new account with email/password, validates password length (min 6 chars)
  - `signOut()` – Signs out current user
  - `getCurrentUser()` – Returns FirebaseUser or null
  - `getCurrentUserModel()` – Converts FirebaseUser to app User model
  - `addAuthStateListener()` / `removeAuthStateListener()` – Methods for monitoring auth state changes
  - `AuthCallback` interface – Callback for async auth operations (onSuccess, onError)
  - Error handling – Converts Firebase Auth error codes to user-friendly messages (invalid email, wrong password, user not found, etc.)
- **Manifest** – Registered CineMatchApplication as the application class.

**Files created/updated:**

- `app/src/main/java/com/example/finalprojectandroiddev2/CineMatchApplication.java` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/data/repository/AuthRepository.java` (new)
- `app/src/main/AndroidManifest.xml` (updated)

**Notes:** AuthRepository uses singleton pattern for app-wide access. Error messages are user-friendly (e.g., "Incorrect password" instead of "ERROR_WRONG_PASSWORD"). Ready for integration with LoginActivity and RegistrationActivity in Step 3.2 and 3.3.

---

## 2025-02-20 – Phase 3 Step 3.2: Login Activity Logic

**What:** Implemented Step 3.2 (Login Activity Logic) from APP_DEV_PLAN.

**Changes:**

- **LoginActivity** – Fully functional login screen with Firebase Authentication integration:
  - **AuthRepository integration** – Connected to AuthRepository singleton for sign-in operations
  - **Input validation** – Email format validation using regex pattern, password length check (min 6 chars), required field validation
  - **Sign-in flow** – Calls `AuthRepository.signIn()` on button click, handles success/error callbacks
  - **Loading state** – Disables sign-in button and input fields during authentication, changes button text to "Signing in…"
  - **Error handling** – Displays Firebase error messages in error TextView, shows field-specific errors in TextInputLayouts
  - **Navigation** – Navigates to HomeActivity on successful login (clears back stack), registration button navigates to RegistrationActivity
  - **UX improvements** – Clears error messages when user types, prevents multiple simultaneous sign-in attempts
- **Strings** – Added error messages: `error_email_required`, `error_email_invalid`, `error_password_required`, `error_password_too_short`, `btn_signing_in`

**Files created/updated:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/LoginActivity.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)

**Notes:** LoginActivity now serves as the entry point for unauthenticated users. All validation happens client-side before calling Firebase Auth. Error messages are user-friendly and displayed both inline (TextInputLayout errors) and globally (error TextView).

**Fix (error messages):** Updated AuthRepository.getErrorMessage() to handle ERROR_INVALID_CREDENTIAL and other common Firebase error codes with user-friendly messages. Replaced technical error codes (e.g., "Authentication failed: ERROR_INVALID_CREDENTIAL") with clear messages (e.g., "Incorrect email or password. Please try again"). Added handling for additional error codes and improved default error message.

---

## 2025-02-20 – Phase 3 Step 3.3: Registration Activity Logic

**What:** Implemented Step 3.3 (Registration Activity Logic) from APP_DEV_PLAN.

**Changes:**

- **RegistrationActivity** – Fully functional registration screen with Firebase Authentication integration:
  - **AuthRepository integration** – Connected to AuthRepository singleton for sign-up operations
  - **Input validation** – Email format validation using regex pattern, password length check (min 6 chars), password confirmation match validation, required field validation
  - **Sign-up flow** – Calls `AuthRepository.signUp()` on button click, handles success/error callbacks
  - **Loading state** – Disables register button and input fields during authentication, changes button text to "Registering…"
  - **Error handling** – Displays Firebase error messages in error TextView, shows field-specific errors in TextInputLayouts (including password mismatch)
  - **Navigation** – Navigates to HomeActivity on successful registration (clears back stack), "Already have an account? Sign In" button navigates to LoginActivity and finishes current activity
  - **UX improvements** – Clears error messages when user types, prevents multiple simultaneous sign-up attempts, clears confirm password error when password field changes
- **Strings** – Added error messages: `error_confirm_password_required`, `error_passwords_not_match`, `btn_registering`

**Files created/updated:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/RegistrationActivity.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)

**Notes:** RegistrationActivity mirrors LoginActivity's functionality but includes password confirmation validation. On successful registration, users are automatically signed in (Firebase Auth handles this) and navigated to HomeActivity. All validation happens client-side before calling Firebase Auth. Error messages are user-friendly and displayed both inline (TextInputLayout errors) and globally (error TextView).

---

## 2025-02-20 – Phase 3 Step 3.4: Auth State Management & Entry Flow

**What:** Implemented Step 3.4 (Auth State Management & Entry Flow) from APP_DEV_PLAN.

**Changes:**

- **SplashActivity** – Auth-aware routing after splash delay:
  - Uses `AuthRepository.getCurrentUser()` to determine if user is authenticated
  - If **authenticated** → starts HomeActivity with `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`, then finishes
  - If **not authenticated** → starts LoginActivity with same flags, then finishes
  - Ensures back stack does not contain Splash; session persistence is left to Firebase (no auto-logout on restart)
- **LoginActivity** – Back-press handling:
  - Registered `OnBackPressedCallback`: first back press shows Toast "Press back again to exit"; second back press within 2 seconds calls `finishAffinity()` to exit app
  - Prevents accidental exit and avoids returning to Splash (Splash already finished and task was cleared)
- **AndroidManifest** – No changes; SplashActivity remains launcher, LoginActivity and RegistrationActivity already declared
- **Strings** – Added `msg_press_back_again_to_exit` for exit confirmation

**Files created/updated:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/splash/SplashActivity.java` (updated)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/LoginActivity.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)

**Notes:** App opens to Splash → then to Login when not authenticated, or to Home when authenticated. Registration remains available from Login. Session persistence is handled by Firebase Auth by default.

---

## 2025-02-20 – User Profiles in Firebase Realtime Database

**What:** Added support for storing additional user details (beyond email/password in Firebase Auth) in Firebase Realtime Database.

**Changes:**

- **Constants** – Added `NODE_USERS = "users"` to represent the root node for user profiles.
- **UserProfile model** – New `UserProfile` data class representing extra user details stored under `users/{uid}`:
  - `uid` – Firebase Authentication user id
  - `name` – Full name (e.g., "Jerome Avecilla")
  - `gender` – Sex/gender string
  - `birthday` – Birthday as an ISO string (e.g., "1998-05-21")
  - `email` – Optional email copy for convenience
- **UserRepository** – New repository for reading/writing user profiles in Realtime Database:
  - Uses `FirebaseDatabase.getInstance(BuildConfig.FB_ROUTE_INSTANCE_URL)` and `Constants.NODE_USERS`
  - `saveUserProfile(UserProfile profile, ProfileSaveCallback)` – Creates/updates `users/{uid}` with profile data
  - `getUserProfile(String uid, ProfileLoadCallback)` – Loads `users/{uid}` as `UserProfile` or returns `null` if not found
  - Logs successes and errors via `Logger` with `TAG_FIREBASE`

**Data structure in Firebase Realtime Database:**

- `users/`
  - `{uid}/`
    - `name`
    - `gender`
    - `birthday`
    - `email` (optional helper field)

**Files created/updated:**

- `app/src/main/java/com/example/finalprojectandroiddev2/utils/Constants.java` (updated)
- `app/src/main/java/com/example/finalprojectandroiddev2/model/UserProfile.java` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/data/repository/UserRepository.java` (new)

**Notes:** Avatar remains a local default drawable (no Firebase Storage integration, as per project constraints). User profiles are not yet wired into Registration or Home screens; those steps will use `UserRepository` to save and display profile details.
