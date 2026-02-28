# CineMatch – Log of Changes

## 2026-02-28 – Bug/UI: Fix Bottom Gradient Gap on Auth & Onboarding Screens

**What:** Resolved an issue where the dark gradient overlay did not extend all the way to the bottom of the screen, leaving a visible gap behind the navigation bar or virtual keyboard.

- Adjusted the edge-to-edge window inset logic in all three authentication activities (`LoginActivity`, `RegistrationActivity`, `OnboardingActivity`).
- Instead of padding the `ScrollView` itself (which truncated the background drawing), the internal form `ConstraintLayout` container is now padded by the `bottomInset`.
- This safely pushes the input fields and buttons above the keyboard while ensuring the surrounding gradient stretches seamlessly to the true bottom edge of the display.

**Files changed:**

- **`res/layout/activity_login.xml`**, **`res/layout/activity_registration.xml`**, **`res/layout/activity_onboarding.xml`** — Added `android:id="@+id/layout_form"` to the inner `ConstraintLayout`.
- **`ui/auth/LoginActivity.java`**, **`ui/auth/RegistrationActivity.java`**, **`ui/onboarding/OnboardingActivity.java`** — Refactored `ViewCompat.setOnApplyWindowInsetsListener` to apply `bottomInset` dynamically to `layoutForm` while keeping `ScrollView` padding at 0.

---

## 2026-02-28 – Feature/UX: Enhance Onboarding Activity UI with Background

**What:** Upgraded the `OnboardingActivity` UI to match the premium "movie night" visual aesthetic established in `WatchActivity`, `LoginActivity`, and `RegistrationActivity`.

- Added the `movie_onboarding_bg.jpg` as a full-screen background image.
- Applied a fade-to-dark `gradient_signup_overlay` to darken the lower half of the screen. The higher transition point (`centerY="0.1"`) perfectly accommodates the three onboarding input fields.
- Restructured the onboarding form, ensuring it stays firmly parked at the bottom of the screen against the dark gradient.
- Added explicit window inset handling in `OnboardingActivity.java` targeting `WindowInsetsCompat.Type.ime()`. The ScrollView gracefully shrinks and pushes the form above the virtual keyboard without distorting the background image.
- **Button Styling:** Restyled the "Continue" button to match the primary application button design (found in HomeActivity) by wrapping it in a `MaterialCardView` with a 50dp corner radius and standardizing the height to 64dp.

**Files changed:**

- **`res/layout/activity_onboarding.xml`** — Converted root to `FrameLayout`, added the background and reused `gradient_signup_overlay`. Pushed constraints to the lower section. Wrapped `btn_continue` in `MaterialCardView` with a 50dp corner radius.
- **`ui/onboarding/OnboardingActivity.java`** — Replaced `BaseActivity` default padding with a custom `WindowInsetsListener` targeting the `ScrollView` to respond to the software keyboard.

---

## 2026-02-28 – Feature/UX: Enhance Registration Activity UI with Background

**What:** Upgraded the `RegistrationActivity` UI to match the premium "movie night" visual aesthetic established in `WatchActivity` and `LoginActivity`.

- Added the `movies_masonry_signup.jpg` as a full-screen background image.
- Applied a fade-to-dark `gradient_signup_overlay` to darken the lower half of the screen. The gradient covers the form container and has a higher transition point (`centerY="0.1"`) to accommodate the taller registration form.
- Restructured the signup form placing the logo, inputs, and buttons in a clean stack parked firmly at the bottom of the screen against the dark gradient.
- Added explicit window inset handling in `RegistrationActivity.java` targeting `WindowInsetsCompat.Type.ime()`. The ScrollView now gracefully shrinks and pushes the form above the virtual keyboard without distorting the background image.
- **Button Styling:** Restyled the "Create Account" button to match the primary application button design (found in HomeActivity) by wrapping it in a `MaterialCardView` with a 50dp corner radius and standardizing the height to 64dp.

**Files changed:**

- **`res/layout/activity_registration.xml`** — Converted root to `FrameLayout`, added the background and new gradient. Pushed constraints to the lower section. Wrapped `btn_register` in `MaterialCardView` with a 50dp corner radius and updated constraints.
- **`res/drawable/gradient_signup_overlay.xml`** — _(NEW)_ Extracted stronger, high-starting gradient specifically to frame the taller registration fields.
- **`ui/auth/RegistrationActivity.java`** — Replaced `BaseActivity` default padding with a custom `WindowInsetsListener` targeting the `ScrollView` to respond to the software keyboard.

---

## 2026-02-28 – Feature/UX: Enhance Login Activity UI with Background

**What:** Upgraded the `LoginActivity` UI to match the premium "movie night" visual aesthetic established in `WatchActivity`.

- Added the `movies_masonry.jpg` as a full-screen background image.
- Applied a fade-to-dark `gradient_login_overlay` to darken the lower half of the screen. The gradient covers the form container rather than the screen, ensuring that the dark area travels up with the form when the software keyboard appears.
- Restructured the login form placing the logo, inputs, and buttons in a clean stack parked firmly at the bottom of the screen against the dark gradient.
- Added explicit window inset handling in `LoginActivity.java` targeting `WindowInsetsCompat.Type.ime()`. The ScrollView now gracefully shrinks and pushes the form above the virtual keyboard without distorting the background image.
- **Button Styling:** Restyled the "Sign In" button to match the primary application button design (found in HomeActivity) by wrapping it in a `MaterialCardView` with a 50dp corner radius and standardizing the height to 64dp.

**Files changed:**

- **`res/layout/activity_login.xml`** — Converted root to `FrameLayout`, added the background and new gradient. Pushed constraints to the lower section. Wrapped `btn_sign_in` in `MaterialCardView` with a 50dp corner radius and updated constraints for `btn_register` to pin to the card.
- **`res/drawable/gradient_login_overlay.xml`** — _(NEW)_ Extracted stronger, high-starting gradient specifically to frame the login fields.
- **`ui/auth/LoginActivity.java`** — Replaced `BaseActivity` default padding with a custom `WindowInsetsListener` targeting the `ScrollView` to respond to the software keyboard.

---

## 2026-02-27 – Feature/UX: Enforce Minimum Members During Swiping Session

**What:** Ensured that a swiping session terminates properly if the lobby member count drops below 2. Previously, if one user left a 2-person lobby, the remaining user would be stuck swiping infinitely with no way to trigger a match since 2 votes are required.

- Fixed the "Exit Session" button in `SwipingActivity` to explicitly call `removeMember` in Firebase before leaving the screen.
- Set up a live listener on the lobby members. If the count ever drops below 2 during an active session, an un-dismissible `AlertDialog` is shown to the remaining user.
- When the remaining user taps "OK" on the alert, they are backed out to the Home screen and removed from the lobby, which triggers Firebase to delete the dead lobby node automatically.

**Files changed:**

- **`res/values/strings.xml`** — Added `title_session_ended` and `msg_not_enough_members`.
- **`ui/swiping/SwipingActivity.java`** — Updated `btnExitSession` to unregister the user, attached the `firebaseRepo.listenMembers` hook to track the active count, and added the `handleNotEnoughMembers()` bailout dialog.

---

## 2026-02-27 – Feature/UX: Block Creating or Joining Multiple Lobbies

**What:** Prevented an edge case where a user already in an active lobby could navigate back to `HomeActivity` and unintentionally create or join a new lobby. This overrides their host status and creates phantom lobby states for other members.

- Added a check for `activeBannerRoomCode` (which determines if the user is in a lobby).
- If the user is in a lobby, tapping Create or Join now surfaces an `AlertDialog` titled "Lobby Action Blocked", informing them they must leave their current lobby first. A "Go to Lobby" button is provided in the dialog.

**Files changed:**

- **`res/values/strings.xml`** — Added `msg_already_in_lobby_title`, `msg_already_in_lobby`, and `btn_go_to_lobby`.
- **`ui/home/HomeActivity.java`** — Updated `btn_create_lobby` and `btn_join_lobby` click listeners to conditionally show the `AlertDialog` instead of navigating. Added `showAlreadyInLobbyDialog()` method.

---

## 2026-02-27 – UI/UX: Rebrand Application Logo

**What:** Replace the legacy application logo with the newly designed CineMatch branding. This update secures our visual identity across all Android system contexts, including the launcher, task switcher, and splash screens. Optimized all density buckets (hdpi to xxhdpi) to ensure crisp rendering on all physical test devices.

**Files changed:**

- **`app/src/main/res/drawable/app_logo_*.png`** — Deployed new branding assets including banners and named logo variants for diverse UI contexts.
- **`app/src/main/res/layout/activity_home.xml`** — Updated `iv_navbar_brand` to use the new `app_logo_banner_orig`.
- **`app/src/main/res/layout/activity_join_lobby.xml`** — Updated `iv_navbar_brand` to use the new `app_logo_banner_orig`.
- **`app/src/main/res/layout/activity_login.xml`** — Updated `iv_navbar_brand` to use the new `app_logo_banner_orig`.
- **`app/src/main/res/layout/activity_onboarding.xml`** — Updated `iv_navbar_brand` to use the new `app_logo_banner_orig`.
- **`app/src/main/res/layout/activity_registration.xml`** — Updated `iv_navbar_brand` to use the new `app_logo_banner_orig`.
- **`app/src/main/res/layout/activity_splash.xml`** — Updated `iv_navbar_brand` to use the new `app_logo_banner_orig`.

---

## 2026-02-27 – Feature/UX: Enhance Lobby "Start Swiping" Button

**What:** Improved the UX for the host in the `LobbyActivity` to clearly indicate when the session can be started. Before, the button was disabled but showed "Start Swiping" with a full active finish. Now, it reads "Waiting for more users…", reduces its opacity to 50%, and sets the text and icon color to `color_text_secondary` when disabled (fewer than 2 members). It restores full opacity, original colors, and "Start Swiping" text once another member joins.

**Files changed:**

- **`res/values/strings.xml`** — Added `btn_waiting_more_users`.
- **`ui/lobby/LobbyActivity.java`** — Updated `setupButtons()` and `updateStartButton()` to dynamically manage the enabled state and text of the `btnStartSwiping` button based on `memberMap.size()`.

---

## 2026-02-26 – Feature/Fix: End of Deck Synchronization and User Progress Counter

**What:** Disabled the "Load More Movies" button for the host until all lobby members have reached the end of the deck, ensuring fairness and preventing the host from skipping ahead. Also added a real-time progress counter displaying how many users have finished voting.

- **Firebase Tracking:** Added a new `endOfDeck` node to Firebase to track which users have reached the end of the deck for a specific page.
- **UI Progress Counter:** Added a `TextView` to the end-of-deck card that displays the count of users who have reached the end of the deck compared to the total members in the lobby (e.g., "3 users already done out of 7").
- **Host Button State:** The "Load More Movies" button for the host is now disabled and displays "Wait for others to finish vote" until all members have reached the end of the deck. Once all members finish, it becomes enabled with the original text.

**Files changed:**

- **`utils/Constants.java`** — Added `NODE_END_OF_DECK`.
- **`res/values/strings.xml`** — Added `btn_waiting_others_vote` and `label_end_of_deck_count`.
- **`data/repository/FirebaseRepository.java`** — Added `markEndOfDeck()` to set/remove user completion status and `listenEndOfDeckForPage()` to track real-time completion. Updated `detachLobbyListeners()`.
- **`res/layout/item_end_of_deck.xml`** — Added `text_end_of_deck_count` `TextView` to display the progress ratio beneath the buttons.
- **`ui/swiping/MovieCardAdapter.java`** — Added tracking for `usersDoneCount` and `totalUsersCount`. Updated `EndOfDeckViewHolder.bind()` to format the progress string, update visibility, and toggle the host's "Load More" button state.
- **`ui/swiping/SwipingActivity.java`** — Updated `fetchMoviesForPage()` to listen to the end-of-deck completion status and update the adapter. Updated `ViewPager2.OnPageChangeCallback` to correctly call `markEndOfDeck(..., true)` when reaching the end of the deck and `markEndOfDeck(..., false)` when swiping back to a movie card.

---

## 2026-02-26 – Fix: SwipingActivity Buttons Visible at End of Deck

**What:** Resolved a UX issue where the "Yes", "No", and "Exit Session" buttons remained visible when the user reached the end of the movie deck (where no voting actions apply).

- **Button Visibility Controller:** Added logic to the `ViewPager2.OnPageChangeCallback` in `SwipingActivity`. When the user swipes to the end-of-deck placeholder card, the bottom controls (`layout_swipe_controls`) and the Exit button (`btn_exit_session`) are set to `View.INVISIBLE` to hide them while preserving layout sizing.
- **Button Restoration:** When real movies are loaded (via the host selecting "Load More") or users swipe back to real movies, the buttons are set back to `View.VISIBLE`.

**Files changed:**

- **`ui/swiping/SwipingActivity.java`** — Bound `layout_swipe_controls` and `btn_exit_session`, and updated visibility conditionally in `onPageSelected()`.

---

## 2026-02-26 – Fix: "Find Another Match" Second Round Sync Bug

**What:** Resolved an issue where tapping "Find Another Match" would successfully restart the swiping session, but an old match would be immediately erroneously detected (showing the previous movie) because old votes and match state persisted in Firebase.

- **Match State Cleared:** Added `clearMatchState()` to `FirebaseRepository`.
- **Session Restart Updated:** When the host clicks "Find Another Match", the app now properly deletes the old `matchedMovieId` and clears all past votes from the `votes` node _before_ advancing the TMDB page and resetting the lobby status to `"swiping"`.

**Files changed:**

- **`data/repository/FirebaseRepository.java`** — Added `clearMatchState(String roomCode, SimpleCallback callback)`.
- **`ui/match/MatchActivity.java`** — Updated `restartSwipingSession()` to asynchronously call `clearMatchState` before pushing the page update and navigating.

---

## 2026-02-26 – Feature/Fix: WatchActivity Play/Pause Toggle and Sync Fix

**What:** Implemented Play/Pause button state toggling for the host in `WatchActivity` and resolved an issue where subsequent plays only worked on the host device.

- **UI Button States:** The host's Watch button now dynamically toggles between "Play" and "Pause" states, complete with corresponding icons (`play_icon`, `pause_icon`).
- **Sync Fix:** Added `LOBBY_STATUS_PAUSED` to Firebase lobby states. When the host toggles playback, it accurately updates the lobby status to `"playing"` or `"paused"`.
- **Member UI Update:** Members see real-time status updates ("Movie is playing" / "Movie is paused") and their `VideoView` correctly starts/pauses in sync with the host for every play attempt, not just the first.

**Files changed:**

- **`utils/Constants.java`** — Added `LOBBY_STATUS_PAUSED`.
- **`res/values/strings.xml`** — Added `btn_pause` and `msg_movie_is_paused`.
- **`res/layout/activity_watch.xml`** — Renamed `btn_play` to `btn_toggle_playback`, added `play_icon`.
- **`ui/watch/WatchActivity.java`** — Added `isPlaying` state toggle, updated click listener to pause/resume `videoIntro` and broadcast status, updated member status listener to handle `LOBBY_STATUS_PAUSED`.

---

## 2026-02-26 – Feature: WatchActivity Synchronized Play and Done Navigation

**What:** Implemented synchronized logic for `WatchActivity` so that the host's actions apply to all members in the lobby.

- **Member UI Adjustment:** Removed the "Done Watching" button for members. They now see a waiting message ("Wait for the host to play the movie").
- **Play Synchronization:** When the host clicks **Play**, the lobby status is updated to `playing`. Members' UI updates to "Movie is playing" and the intro video begins playing for them as well.
- **Done Watching Synchronization:** When the host clicks **Done Watching**, the lobby status is updated to `completed` in Firebase, rather than leaving the room stuck in "watching". All members' screens react to this state change and automatically navigate back to the `HomeActivity`, closing out the session properly for everyone.

**Files changed:**

- **`utils/Constants.java`** — Added `LOBBY_STATUS_PLAYING` and `LOBBY_STATUS_COMPLETED`.
- **`res/values/strings.xml`** — Added `msg_wait_host_play` and `msg_movie_is_playing`.
- **`res/layout/activity_watch.xml`** — Placed host actions inside `layout_host_actions` and added `text_member_status` for members.
- **`ui/watch/WatchActivity.java`** — Updated `setupButtons()` for host/member layout split. Add `listenForStatusChanges()` to monitor Firebase status and update UI/navigate to HomeActivity simultaneously for all members.

---

## 2026-02-26 – Feature: Watch Activity + Synchronized "Watch Now" Navigation

**What:** Implemented `WatchActivity` where users arrive after the host clicks "Watch Now" on the Match screen. The screen shows a mock intro video using `VideoView` referencing a raw resource.

**Files changed:**

- **`utils/Constants.java`** — Added `LOBBY_STATUS_WATCHING = "watching"`
- **`ui/watch/WatchActivity.java`** _(NEW)_ — Handles `VideoView` playback of raw MP4, sets up host controls (Play / Done Watching) and member info text. Syncs movie info layout pattern.
- **`res/layout/activity_watch.xml`** _(NEW)_ — Video layout and movie details, reusing styles from Match screen.
- **`ui/match/MatchActivity.java`** — Updated `setupButtons` so "Watch Now" changes lobby status to `watching` and calls `navigateToWatch(true)`. Added status listener for members to call `navigateToWatch(false)`.
- **`AndroidManifest.xml`** — Registered `.ui.watch.WatchActivity`
- **`res/values/strings.xml`** — Added `watch_info_alone`, `watch_info_others`, `btn_play`, `btn_done_watching`.
- **`res/raw/cinematch_default_intro_video.mp4`** _(MOVED)_ — Moved from `drawable/` because it's a binary media resource, to prevent AAPT bundle merge errors.

---

## 2026-02-26 – Fix: Members Stuck on Match After Host Starts New Round

**Problem:** After the host tapped **Find Another Match**, only the host was redirected back to `SwipingActivity`. Members remained stuck on `MatchActivity` even though Firebase showed `status: "swiping"` and `currentPage` had advanced.

**Root cause:**  
`FirebaseRepository` is a singleton. `SwipingActivity.onDestroy()` was calling `firebaseRepo.detachListeners()`, which detached **all** active Firebase listeners, including the lobby `status` listener that **MatchActivity** had just attached for members. As a result, members never received the `"swiping"` status change needed to navigate back to swiping.

**Fix:**

1. **Scoped listener cleanup:**
   - Added `detachSwipingListeners()` in `FirebaseRepository` that only detaches:
     - Vote listener (`votes/{movieId}`)
     - Page listener (`currentPage`)
   - Updated `detachListeners()` Javadoc and left it for rare full cleanups.

2. **SwipingActivity:**
   - `onDestroy()` now calls:
     - `firebaseRepo.detachSwipingListeners();`
   - This ensures page/vote listeners are cleaned up when leaving swiping, **without** touching the lobby `status` listener that other activities may own.

3. **MatchActivity:**
   - When members auto-navigate back to swiping on `status = "swiping"`:
     - `navigateBackToSwipingAsMember()` now calls `firebaseRepo.detachLobbyListeners()` before starting `SwipingActivity`.
   - When the host taps **Find Another Match**:
     - `restartSwipingSession()` now calls `firebaseRepo.detachLobbyListeners()` just before starting `SwipingActivity`.
   - This keeps MatchActivity's own `members + status` listeners tidy without interfering with swiping-owned listeners.

**Files changed:**

- **`data/repository/FirebaseRepository.java`**
  - Added `detachSwipingListeners()` and documented why status must not be detached there.

- **`ui/swiping/SwipingActivity.java`**
  - `onDestroy()` now calls `detachSwipingListeners()` instead of `detachListeners()`.

- **`ui/match/MatchActivity.java`**
  - `navigateBackToSwipingAsMember()` and `restartSwipingSession()` call `detachLobbyListeners()` before starting `SwipingActivity`.

**Result:** When the host starts a new round from the match screen, **all members** reliably receive the `"swiping"` status update and are redirected back to `SwipingActivity` in sync with the host. No one gets stuck on `MatchActivity` anymore.

---

## 2026-02-26 – Feature: Find Another Match → New Swiping Round (Page +1)

**What:** Implemented full **Find Another Match** behaviour so the host can start a **new swiping round** from `MatchActivity` and automatically bring all members back into `SwipingActivity` with a **fresh deck** of movies based on the **next TMDB page**.

**Behaviour:**

1. Host taps **Find Another Match** on the match screen.
2. App reads `lobbies/{roomCode}/currentPage` from Firebase to determine the last TMDB page used in the previous round.
3. Computes `nextPage = (currentPage <= 0 ? 1 : currentPage + 1)` so the next round starts on the **next** page (new movies).
4. Writes `currentPage = nextPage` and sets `status = "swiping"` for the lobby.
5. Host navigates to `SwipingActivity` with an explicit `EXTRA_INITIAL_PAGE = nextPage`.
6. All members currently on `MatchActivity` listen for `status = "swiping"` and automatically navigate back to `SwipingActivity`.
7. In `SwipingActivity`, if the user is host and `EXTRA_INITIAL_PAGE` is present, that page is used as the initial TMDB page instead of the room-code hash; the host fetches `nextPage` and broadcasts it to Firebase so all members fetch the same page via `listenForPageChanges()`.

**Files changed:**

- **`data/repository/FirebaseRepository.java`**
  - Added `getCurrentPage(String roomCode, PageCallback)` — one-shot read of `lobbies/{roomCode}/currentPage` (returns 0 if missing).

- **`ui/swiping/SwipingActivity.java`**
  - Added `EXTRA_INITIAL_PAGE` constant.
  - Updated `onCreate()` so that for hosts:
    - If `EXTRA_INITIAL_PAGE > 0`, uses this as the initial TMDB page.
    - Otherwise, falls back to the room-code hash–derived page.
  - Host still calls `fetchMoviesForPage(initialPage)` and `setCurrentPage(roomCode, initialPage)` so members receive the page change through the existing `listenForPageChanges()` path.

- **`ui/match/MatchActivity.java`**
  - Updated **Find Another Match** click handler:
    - Reads `currentPage` via `firebaseRepo.getCurrentPage(roomCode, callback)`.
    - Computes `nextPage`, writes it back to `currentPage`, and sets lobby `status` to `"swiping"`.
    - Starts `SwipingActivity` for the host with `EXTRA_ROOM_CODE`, `EXTRA_IS_HOST = true`, and `EXTRA_INITIAL_PAGE = nextPage`.
  - Added a **status listener for members**:
    - Non-host devices on `MatchActivity` call `listenLobbyStatus()` and, when status becomes `"swiping"`, automatically start `SwipingActivity` with the same `roomCode` and `EXTRA_IS_HOST = false`.

**Result:** After a match is found and everyone lands on the match screen, the host can tap **Find Another Match** to seamlessly start a new voting round on the next TMDB page. All members are pulled back into `SwipingActivity` automatically, and all devices see the same new deck of movies.

---

## 2026-02-26 – Bug Fix: MatchActivity Back Button & Predictive Back Gesture

**Problem:** Users could still exit `MatchActivity` by swiping from the left edge (predictive back gesture) or pressing the system back button, even though `onBackPressed()` was overridden. This was because the `OnBackPressedCallback` was added with `enabled=true` but the callback itself was empty — the system still registered a back gesture and triggered the default back behavior (finishing the activity).

**Root cause:** The `OnBackPressedCallback` was not properly disabled or configured to block the back gesture. The default back behavior was still being invoked.

**Fix:**

1. Removed the `OnBackPressedCallback` entirely
2. Added `@Override public void onBackPressed()` to `MatchActivity` and made it an empty no-op (this blocks the hardware/software back button)
3. Added a comment explaining that predictive back gestures are not explicitly blocked but the activity is now fully protected against accidental back navigation

**Files changed:**

- **`ui/match/MatchActivity.java`** — removed `OnBackPressedCallback` registration; added `@Override public void onBackPressed()` no-op method

---

## 2026-02-25 – UI Tweak: Make Movie Overview Fully Scrollable in MatchActivity

**What:** Removed the 4-line cap (`maxLines="4"`, `ellipsize="end"`) from the overview `TextView` in `activity_match.xml`. The bottom info panel is already inside a `ScrollView`, so the full overview text now displays in its entirety and the user can scroll the panel naturally to read it — no ellipsis cutoff.

**Files changed:**

- **`res/layout/activity_match.xml`** — removed `android:maxLines="4"` and `android:ellipsize="end"` from `text_movie_overview`

---

## 2026-02-25 – UI Decision: Remove Leave Button from MatchActivity

**What:** Removed the Leave button from both host and member action sections in `MatchActivity`. The rationale: users arrive at the match screen because they actively voted for the movie — a Leave button at this point is semantically wrong and creates data-integrity questions (does leaving remove votes/members?). The match screen is now a commitment screen.

**Host layout** (after): Full-width **Watch Now** pill → **Find Another Match** text link below.
**Member layout** (after): **"Wait for the host to start watch the movie"** info text only — no buttons.

**Back press disabled:** Added `onBackPressed()` override (no-op) so users cannot accidentally navigate away from the match screen.

**Files changed:**

- **`res/layout/activity_match.xml`** — removed `btn_leave_lobby` card+button from `layout_host_actions`; removed `btn_leave_member` card+button from `layout_member_actions`; `Watch Now` now occupies full width; member section is text-only
- **`ui/match/MatchActivity.java`** — removed all `btn_leave_lobby` / `btn_leave_member` click listeners; deleted `leaveLobby()` helper; added `@Override onBackPressed()` no-op

---

## 2026-02-25 – UI Enhancement: Match Activity Redesign

**What:** Overhauled the entire `MatchActivity` UI to match the cinematic aesthetic of the swiping card and lobby screens.

**Files changed:**

- **`res/layout/activity_match.xml`** _(REDESIGNED)_ — restructured root from `FrameLayout` to vertical `LinearLayout` so the celebration card sits physically **above** the movie image; image area uses `layout_weight=1` inside a `FrameLayout` with a darker `gradient_match_overlay` scrim; bottom info panel (title, date, rating, overview, genre chips) overlays the image at the bottom; removed all emojis; replaced emoji icons with `calendar_icon` and `star_icon` drawables; genre chips match the swiping card style; added two role-based action groups:
  - `layout_host_actions` — side-by-side Leave (outlined red) + Watch Now (filled teal) pill buttons + "Find Another Match" text link
  - `layout_member_actions` — full-width Leave pill + "Wait for the host to start watch the movie" info text
- **`res/layout/activity_match.xml` — celebration card** — replaced plain `TextView` with `MaterialCardView` (`color_surface`, 16dp radius); header row has "It's a Match!" start-aligned on the left and `users_icon` + live member count (`2/2`) on the right; subtitle "Everyone agreed on this one!" below, start-aligned
- **`res/drawable/gradient_match_overlay.xml`** _(NEW)_ — darker gradient scrim variant (`#55` top → `#CC` center → `#F2` bottom) dedicated to MatchActivity so movie backdrops with logo text don't bleed through the overlay
- **`res/drawable/bg_rounded_poster.xml`** _(NEW)_ — placeholder shape drawable for the poster ImageView
- **`res/drawable/users_icon.xml`** — fixed invalid XML comment (`--` inside block comment) that caused `parseDebugLocalResources` build failure
- **`ui/match/MatchActivity.java`** — applied Glide `RoundedCorners(16dp)` transform for poster rounding; date formatter changed from year-only to `"MMM yyyy"` (e.g. "Feb 2025"); rating text is now a plain number (icon in layout); `setupButtons()` branches on `isHost` to show/hide the correct action group and wire all four buttons (host Leave, Watch Now, Find Another Match, member Leave); added `startMemberCountListener()` which does a one-shot `loadAllMembers` then a live `listenMembers` callback to display and update the member count in real-time

---

## 2026-02-25 – Bug Fix: MatchActivity Showed Member UI for All Users (Issue #45)

**Problem:** After a match was detected, `SwipingActivity` navigated all devices to `MatchActivity` but did **not** pass `EXTRA_IS_HOST` in the Intent. As a result, `MatchActivity.isHost` defaulted to `false` on every device, and both the host and members saw the member-only UI (full-width Leave button + "Wait for the host…" text). The host's Watch Now and Find Another Match buttons were never shown.

**Root cause:** `SwipingActivity.navigateToMatch()` only passed `EXTRA_ROOM_CODE`; `EXTRA_IS_HOST` was omitted.

**Fix:** Added one line to `navigateToMatch()`:

```java
intent.putExtra(LobbyActivity.EXTRA_IS_HOST, isHost);
```

**Files changed:**

- **`ui/swiping/SwipingActivity.java`** — added `intent.putExtra(LobbyActivity.EXTRA_IS_HOST, isHost)` to `navigateToMatch()`

---

## 2026-02-24 – Feature: Phase 8.3 – Match Activity Implementation

**What:** Fully implemented `MatchActivity` — displays the matched movie, plays a Lottie confetti animation, and provides Watch on TMDb / Find Another Match / Leave Lobby actions.

**Files changed:**

- **`ui/match/MatchActivity.java`** — reads `matchedMovieId` from Firebase via `getMatchedMovieId()`, fetches full movie details from TMDB `GET /movie/{id}`, binds poster (Glide), title, `⭐ X.X` rating, release year, and overview; plays Lottie confetti on `onCreate`; "Watch on TMDb" opens browser intent; "Find Another Match" resets Firebase (`currentPage=0`, `status="swiping"`) and starts a new `SwipingActivity`; "Leave Lobby" calls `finishAffinity()` → `HomeActivity`
- **`res/layout/activity_match.xml`** _(REDESIGNED)_ — `FrameLayout` root with scrollable detail content + full-screen `LottieAnimationView` overlay (touch-passthrough); poster wrapped in `MaterialCardView`, details in a second card
- **`assets/confetti.json`** _(NEW)_ — hand-crafted Lottie animation: 6 coloured paper pieces falling + fading over 2 s
- **`data/api/TmdbApiService.java`** — added `getMovieDetails(movieId, language, bearer)` endpoint reusing `Movie` model
- **`app/build.gradle.kts`** — added `com.airbnb.android:lottie:6.4.1`
- **`res/values/strings.xml`** — added `msg_match_subtitle`; renamed `btn_watch_now` → "Watch on TMDb"

---

## 2026-02-24 – Feature: Phase 8.2 – Match Event Handling

**What:** Verified full match event pipeline established in Phase 7.3 is complete — no new code required for 8.2.

**Pipeline:** `handleYes()` → `recordVote()` → `checkForMatch()` (via `MatchDetector`) → writes `matchedMovieId` + sets `status = "matched"` → `listenForMatch()` on all devices → `navigateToMatch()` → `MatchActivity` + `finish()`.

**No files changed** (all implemented in Phase 7.3).

---

## 2026-02-24 – Feature: Phase 8.1 – Match Detection Logic

**What:** Extracted match-condition logic into `MatchDetector` utility class; added `getMatchedMovieId()` to `FirebaseRepository` for use by `MatchActivity`.

**Member-leaves edge case:** Handled correctly — `checkForMatch()` performs a live snapshot read at vote time, so departed members are already absent from `members/` and the required vote count naturally drops.

**Files changed:**

- **`utils/MatchDetector.java`** _(NEW)_ — `isMatch(voteCount, memberCount)`, `memberCount(snapshot)`, `voteCount(snapshot, movieId)` static helpers
- **`utils/Constants.java`** — added `NODE_MATCHED_MOVIE_ID = "matchedMovieId"`
- **`data/repository/FirebaseRepository.java`** — `checkForMatch()` delegates to `MatchDetector`; raw string literal replaced with `Constants.NODE_MATCHED_MOVIE_ID`; added `getMatchedMovieId(roomCode, callback)` + `MatchedMovieCallback` interface

---

## 2026-02-24 – Redesign: Unified Page Sync Architecture (v5 — final)

**Problem:** Members were stuck on the end-of-deck card after the host loaded more movies. Two root causes:

1. **Two separate fetch paths:** Host fetched locally then broadcast; members listened and fetched separately. Race conditions between async `isHost` resolution and Firebase listener attachment made guards unreliable.
2. **Singleton listener detachment:** `LobbyActivity.onDestroy()` called `firebaseRepo.detachListeners()` on the singleton `FirebaseRepository`, which killed the page listener that `SwipingActivity` had just attached during the lobby→swiping activity transition.

**Solution:**

- **Hybrid fetch model:** Host fetches movies locally (in `onCreate` + `loadMoreMovies`) and writes `currentPage` to Firebase for member sync. Members react to Firebase `currentPage` changes via `listenForPageChanges()` → `fetchMoviesForPage()`.
- **`isHost` from Intent:** Read immediately from `EXTRA_IS_HOST` instead of async Firebase `loadAllMembers()` resolution — eliminates timing issues.
- **Stale page reset:** Both `LobbyActivity` and `CreateLobbyActivity` write `currentPage = 0` before starting the session, clearing stale values from previous sessions.
- **Scoped listener cleanup:** Added `detachLobbyListeners()` to `FirebaseRepository` — only detaches members + status listeners. Lobby activities now call this instead of `detachListeners()`, preserving SwipingActivity's page + votes listeners across the activity transition.

**Files changed:**

- **`ui/swiping/SwipingActivity.java`** — removed `fetchMovies()`; added `fetchMoviesForPage()` unified TMDB fetch + `initialLoadDone` flag; host fetches locally in `onCreate()` and `loadMoreMovies()` then broadcasts; member fetches via listener; `isHost` from Intent; removed `loadedPages` HashSet
- **`data/repository/FirebaseRepository.java`** — added `detachLobbyListeners()` (members + status only)
- **`ui/lobby/LobbyActivity.java`** — `startSwipingSession()` resets `currentPage` to 0; `onDestroy()` calls `detachLobbyListeners()`
- **`ui/lobby/CreateLobbyActivity.java`** — same `currentPage` reset + `detachLobbyListeners()` in both `leaveLobby()` and `onDestroy()`

---

## 2026-02-24 – Fix: Load More Button Required Multiple Clicks Before New Cards Appeared

**Problem:** Tapping "Load More Movies" required 3–4 clicks before the new cards were displayed. Each click loaded movies and broadcast the page to Firebase (confirmed in logs), but the ViewPager2 never advanced to the new cards on the first click.

**Root cause:** `notifyItemRangeInserted()` schedules a layout pass for the **next frame**, but `setCurrentItem()` was called in the **same frame** before RecyclerView processed the insert. ViewPager2 saw `currentPosition == firstNewPos` (a no-op) because it hadn't shifted its internal position yet.

**Fix:** Wrapped `setCurrentItem(firstNewPos)` in `viewPagerMovies.post(...)` to defer it until after the layout pass completes. Applied to both `loadMoreMovies()` (host) and `listenForPageChanges()` (member).

**Files changed:**

- **`ui/swiping/SwipingActivity.java`** — `loadMoreMovies()` and `listenForPageChanges()` use `viewPagerMovies.post(() -> viewPagerMovies.setCurrentItem(firstNewPos, true))`

---

## 2026-02-24 – Fix: End-of-Deck Shown as ViewPager2 Card Instead of Overlay

**Problem:** End-of-deck message was an overlay on top of the last movie card (visible while the card was still being viewed). Also appeared as soon as user arrived at card 20, not after swiping it.

**Fix:** Removed overlay entirely. End-of-deck is now a real **ViewPager2 card** (last slot in the adapter) that becomes visible only after `advanceCard()` scrolls past the final real movie.

**Changes:**

- **`res/layout/item_end_of_deck.xml`** _(NEW)_ — full-card layout with 🍿 emoji, title, subtitle, host Load More button (`reload_icon`, TextButton style), member waiting text (`color_text_secondary`)
- **`res/layout/activity_swiping.xml`** — removed `layout_end_of_deck` overlay `LinearLayout`; ViewPager2 is now the only child of its `FrameLayout`
- **`ui/swiping/MovieCardAdapter.java`** — class now extends `RecyclerView.Adapter<RecyclerView.ViewHolder>`; added `TYPE_MOVIE=0` / `TYPE_END_OF_DECK=1`; added `EndOfDeckCallback` interface; added `isHost` + `setIsHost()` / `setEndOfDeckCallback()`; `getItemCount()` returns `movies.size() + 1`; `onCreateViewHolder` inflates movie or end-of-deck layout; `onBindViewHolder` dispatches to `MovieCardViewHolder` or `EndOfDeckViewHolder`; added `EndOfDeckViewHolder` inner class that shows host button or member text and wires click callback
- **`ui/swiping/SwipingActivity.java`** — removed `layoutEndOfDeck`, `btnLoadMore`, `textWaitingHost` fields / `bindViews()` / `setupButtons()` references; removed `showEndOfDeck()` method; removed `isLastCard` check from `onPageSelected`; wired `setEndOfDeckCallback(() -> loadMoreMovies())` in `setupViewPager()`; added `movieCardAdapter.setIsHost(isHost)` after member load; cleaned dead `layoutEndOfDeck.setVisibility` / `btnLoadMore.setEnabled` calls from `loadMoreMovies()` and `listenForPageChanges()`

---

## 2026-02-24 – Feature: Phase 7.5 – Movie Queue Management / End-of-Deck + Load More

**What:** When users swipe through all 20 initial movies without a match, an "End of Deck" overlay appears. The host sees a **Load More Movies** button to load the next TMDB page. Members see a waiting hint until the host loads more.

**End-of-deck flow:**

1. `onPageSelected` detects last card → `showEndOfDeck()`
2. Host: `btn_load_more` visible → tap → `loadMoreMovies()` → TMDB fetch → `appendMovies()` → `setCurrentPage()` in Firebase → overlay hidden
3. Members: `textWaitingHost` visible → `listenForPageChanges()` fires → fetch same page → `appendMovies()` → overlay hidden
4. `appendMovies()` deduplicates by movie ID — no repeats even if pages overlap

**Files modified:**

- **`utils/Constants.java`** — added `NODE_CURRENT_PAGE = "currentPage"` for Firebase sync
- **`ui/swiping/MovieCardAdapter.java`** — added `appendMovies(List<Movie>)` — deduplicates by TMDB ID using a `HashSet`, uses `notifyItemRangeInserted` for efficient update
- **`res/layout/activity_swiping.xml`** — added `layout_end_of_deck` overlay (GONE by default) with title, subtitle, `btn_load_more` (host, reload_icon, TextButton style), and `text_waiting_host` (member, color_text_secondary)
- **`res/values/strings.xml`** — added `btn_load_more`, `label_end_of_deck_title` ("That's all for now! 🍿"), `label_end_of_deck_subtitle`, `label_waiting_host_load_more` ("Waiting for the host to load more movies… Hang tight!")
- **`data/repository/FirebaseRepository.java`** — added `PageCallback` interface; `setCurrentPage(roomCode, page)` — writes `lobbies/{roomCode}/currentPage`; `listenCurrentPage(roomCode, callback)` — ValueEventListener on same node; `detachPageListener()`; `detachListeners()` updated
- **`ui/swiping/SwipingActivity.java`** — added `isHost`, `currentPage`, overlay view fields; `isHost` set from `memberMap` in `loadMembersAndStartVoteSync()` via `LobbyMember.isHost()`; `showEndOfDeck()` — conditionally shows host or member UI; `loadMoreMovies()` — increments page, fetches TMDB, appends, broadcasts; `listenForPageChanges()` — members react to host's page increment by fetching same page; `setupButtons()` wires `btnLoadMore`; `onPageSelected` detects last card and calls `showEndOfDeck()`; `listenForPageChanges()` called in `onCreate`

---

## 2026-02-24 – Feature: Phase 7.4 – Real-time Vote Sync (SwipingActivity + FirebaseRepository)

**What:** The status bar at the top of the swiping screen now shows who has voted Yes on the currently visible movie in real-time, across all lobby members.

**Status bar format:** `James ✓  ·  You ✓  ·  2/3 voted`

- Current user shown as "You"
- Names resolved from Firebase member map
- "Waiting for votes…" shown when no one has voted yet on this movie

**Vote sync flow:**

1. `loadMembersAndStartVoteSync()` — called on `onCreate`, fetches all lobby members once (`loadAllMembers()`) and then starts the vote listener for card 0
2. `attachVoteSyncForMovie(movieId)` — calls `listenVotesForMovie()` in FirebaseRepository; detaches the previous movie's listener automatically before attaching the new one
3. `onPageSelected` callback on ViewPager2 — re-attaches vote listener for each new card as user advances
4. Race condition guard in `fetchMovies()` — if member map is already loaded when movies arrive, kicks off vote sync immediately; similarly `loadMembersAndStartVoteSync()` only starts the listener if `currentMovies` is already set

**Files modified:**

- **`data/repository/FirebaseRepository.java`** — added `VotesCallback` interface (`onVotesUpdated(Set<String>)`); `AllMembersCallback` interface (`onLoaded(Map<String,LobbyMember>)`); `activeVotesListener/Ref` tracking fields; `loadAllMembers()` one-shot read returning userId→LobbyMember map; `listenVotesForMovie()` ChildEventListener on `votes/{movieId}/` maintaining a live `LinkedHashSet<String>` of voter UIDs, firing callback on each add/remove; `detachVotesListener()` cleanup; `detachListeners()` updated to also call `detachVotesListener()`
- **`ui/swiping/SwipingActivity.java`** — added `TextView tvMemberStatus`, `Map<String,LobbyMember> memberMap` fields; bound `text_member_status` in `bindViews()`; added `loadMembersAndStartVoteSync()`, `attachVoteSyncForMovie()`, `updateVoteStatusBar()`; registered `OnPageChangeCallback` in `setupViewPager()` to re-attach vote listener on page change; race guard in `fetchMovies()` after movies are set

---

## 2026-02-24 – Feature: Phase 7.3 – Vote Recording (SwipingActivity + FirebaseRepository)

**What:** Yes votes are now persisted to Firebase Realtime Database. No votes are intentionally discarded. After each Yes vote, the app automatically checks for a unanimous match across all lobby members and navigates all devices to `MatchActivity` when one is found.

**Firebase schema additions:**

```
lobbies/{roomCode}/votes/{movieId}/{userId} = true   ← Yes vote
lobbies/{roomCode}/matchedMovieId = "{movieId}"      ← written on match
lobbies/{roomCode}/status = "matched"                ← triggers navigation on all devices
```

**Vote flow:**

- User swipes right or taps **Yes** → `handleYes()` → `FirebaseRepository.recordVote()` writes `votes/{movieId}/{userId} = true`
- After write: `checkForMatch()` reads the full lobby snapshot, compares `votes/{movieId}` child count vs `members/` child count
- If equal → `matchedMovieId` is written + `status` set to `"matched"`
- All devices listen via `listenForMatch()` → `listenLobbyStatus()` → status becomes `"matched"` → `navigateToMatch()` → `MatchActivity`
- User swipes left or taps **No** → `handleNo()` → card advances silently (nothing written)

**Duplicate tap guard:** Yes button is disabled before async Firebase write and re-enabled in the callback (success or error), preventing double votes.

**Files modified:**

- **`data/repository/FirebaseRepository.java`** — added `VoteCallback` interface (`onVoteRecorded`, `onMatchFound`, `onError`); `recordVote(roomCode, userId, movieId, callback)` writes the vote then calls `checkForMatch()`; `checkForMatch()` reads lobby snapshot, compares counts, writes `matchedMovieId` + sets status to `"matched"` if all members voted Yes; updated schema Javadoc comment
- **`ui/swiping/SwipingActivity.java`** — replaced Phase 7.2 stubs: real `handleYes()` with Firebase write + button debounce, `handleNo()` (advance only); added `listenForMatch()` via `listenLobbyStatus()`, `navigateToMatch()` to `MatchActivity` with `EXTRA_ROOM_CODE`; `onDestroy()` now calls `firebaseRepo.detachListeners()`; added `currentMovies` field to resolve movie ID from current card position; added `FirebaseAuth`, `FirebaseRepository`, `MatchActivity`, `Constants` imports

---

## 2026-02-24 – Feature: Phase 7.2 – Swipe Gesture Detection (SwipingActivity)

**What:** Movie cards now support swipe-left (No) and swipe-right (Yes) gestures directly on the card view, with animated visual feedback and fly-off / snap-back transitions.

**Gesture flow:**

- **Drag right** → card translates + rotates clockwise → **YES ✓** label fades in (green)
- **Drag left** → card translates + rotates counter-clockwise → **NO ✗** label fades in (red)
- **Release ≥ 120dp** → `flyOff()` animation (card flies off-screen) → fires `SwipeCallback` → `advanceCard()`
- **Release < 120dp** → `snapBack()` animation (card springs back to rest)
- **Short tap (no drag)** → toggles expanded/collapsed info panel (unchanged)

**Architecture:**

- `MovieCardAdapter.SwipeCallback` interface (`onSwipedYes()` / `onSwipedNo()`) decouples gesture detection from vote logic — both swipe gesture AND Yes/No button tap call the exact same `handleYes()` / `handleNo()` in `SwipingActivity`. Ready for Step 7.3 vote recording with zero changes to gesture code.
- `ViewPager2.setUserInputEnabled(false)` is retained — ViewPager2's own swipe is still off. Gestures are handled by `OnTouchListener` on the card view itself.

**Files created:**

- **`res/drawable/bg_swipe_label_yes.xml`** _(new)_ — Rounded green-bordered semi-transparent background for YES label
- **`res/drawable/bg_swipe_label_no.xml`** _(new)_ — Rounded red-bordered semi-transparent background for NO label

**Files modified:**

- **`res/layout/item_movie_card.xml`** — Added `overlay_swipe_yes` (`start|top`, −20° tilt, green) and `overlay_swipe_no` (`end|top`, +20° tilt, red); both `INVISIBLE` by default
- **`ui/swiping/MovieCardAdapter.java`** — Full rewrite: added `SwipeCallback` interface, `attachTouchListener()` per card (translate, rotate, overlay alpha), `flyOff(boolean isYes)`, `snapBack()`, `resetCard()` helpers; constants: `SWIPE_THRESHOLD_DP=120`, `MAX_ROTATION_DEG=20`, `FLY_DURATION_MS=280`, `SNAP_DURATION_MS=250`
- **`ui/swiping/SwipingActivity.java`** — Registered `SwipeCallback` in `setupViewPager()` routing `onSwipedYes()→handleYes()` and `onSwipedNo()→handleNo()`

---

## 2026-02-24 – SwipingActivity: Lock Swipe to Yes/No Buttons Only

**What:** Users could freely swipe between movie cards on the ViewPager2 without voting. This broke the voting mechanic — skipped cards would have no recorded vote.

**Fix:** Added `viewPagerMovies.setUserInputEnabled(false)` in `setupViewPager()`. Cards now only advance when the **Yes** or **No** button is tapped (via `advanceCard()`). The ViewPager2 still animates programmatically.

**Files changed:**

- **`ui/swiping/SwipingActivity.java`** — added `setUserInputEnabled(false)` after adapter is set.

**Phase 7.2 note:** Swipe-left = No / swipe-right = Yes gesture shortcuts will be added directly to the card view (not ViewPager2) so the vote handler is always invoked.

---

## 2026-02-24 – SwipingActivity: Room-Code-Seeded TMDB Page for Session Variety

**What:** All devices always fetched TMDB trending page 1, which worked but gave the same 20 movies every session with no variety.

**Approach:** The page number (1–100) is now derived deterministically from the room code hash:

```java
int page = (Math.abs(roomCode.hashCode()) % 100) + 1;
```

Same lobby = same room code = same hash = same page = **identical ordered movie list on all devices** — no Firebase coordination needed. Different lobbies get different pages automatically.

**Files changed:**

- **`ui/swiping/SwipingActivity.java`** — replaced hardcoded `page=1` with room-code-derived page; falls back to page 1 for solo/test sessions.

---

## 2026-02-24 – Reverted: Firebase Shared Movie Queue (Abandoned)

**What was attempted:** The host would fetch movies from TMDB (random page + shuffle), write them to Firebase `/lobbies/{code}/movies/`, set status to `"swiping"`, and all devices (including host) would read the shared list. This was designed to guarantee identical shuffled decks.

**Why reverted:** Members' `listenMovieQueue()` read arrived before Firebase propagated the host's write — always returning "Movie queue not found". The TMDB-fallback workaround also triggered because the host's `startSwipingSession()` was not wired in time. The Firebase-write approach added unnecessary async complexity for a problem already solved deterministically via room-code-seeded page numbers.

**Files that remain with residual additions (kept for Phase 7.2 use):**

- **`data/repository/FirebaseRepository.java`** — `saveMovieQueue()`, `listenMovieQueue()`, `MovieQueueCallback` retained (needed for vote recording).
- **`data/model/Movie.java`** — Setters added (needed for Firebase deserialization of votes).

---

## 2026-02-24 – MovieCardAdapter: Release Date Format + Star Rating Badge + Default Expanded State

**What:** Three UI polish changes to the movie swipe card:

1. **Date format** — `formatReleaseDate()` now parses `"YYYY-MM-DD"` from TMDB and reformats to `"MMM yyyy"` (e.g. `"Feb 2026"`) using `SimpleDateFormat`.

2. **Star rating badge** — Added a `text_movie_rating` `TextView` in the top-right corner of `item_movie_card.xml` with a semi-transparent dark background and white text. Adapter binds `⭐ X.X` from `movie.getVoteAverage()`.

3. **Default expanded state** — Card now opens in the **expanded** state (title + date + overview + genres visible). Tapping collapses it; tapping again expands it. Previously the default was collapsed.

**Files changed:**

- **`res/layout/item_movie_card.xml`** — Added `text_movie_rating` `TextView` (top-right, semi-transparent bg, white text).
- **`ui/swiping/MovieCardAdapter.java`** — `isExpanded = true` default; updated `formatReleaseDate()`; added `tvRating` binding.

---

## 2026-02-24 – TmdbApiService: Added `page` Parameter to `getTrendingMovies`

**What:** `getTrendingMovies()` previously only accepted `timeWindow`, `language`, and the auth header. Added an `int page` `@Query` parameter so callers can specify which result page to fetch.

**Callers updated:**

- **`ui/swiping/SwipingActivity.java`** — passes room-code-derived page.
- **`ui/home/HomeActivity.java`** — passes `page = 1` (unchanged behaviour).

**Files changed:**

- **`data/api/TmdbApiService.java`** — added `@Query("page") int page` to `getTrendingMovies` signature.

---

**What:** Redesigned the movie swipe card with a transparent background (blends into the black activity background) and a two-state bottom overlay for movie info.

**State 1 — Collapsed (default):** Bottom-quarter black gradient overlay shows movie title (white, wrapped) and release date (📅 calendar icon + `color_text_secondary`).

**State 2 — Expanded (tap to reveal):** Overlay expands to also show the full movie overview (wrapped, semi-transparent white) and genre chips (same `ChipGroup` style as `item_movie_popular`). Tapping again collapses it.

**Files created/modified:**

- **`res/drawable/gradient_card_overlay.xml`** _(new)_ — strong #EE000000 bottom-to-transparent black gradient for the card scrim
- **`res/layout/item_movie_card.xml`** _(rewritten)_ — transparent `FrameLayout` root, full-bleed backdrop, gradient scrim, `LinearLayout` info block with two-state visibility
- **`ui/swiping/MovieCardAdapter.java`** _(rewritten)_ — `isExpanded` toggle per `ViewHolder`, full TMDB genre ID→name map (19 genres), chips inflated inline matching `item_movie_popular` style, card resets to collapsed on rebind
- **`res/values/strings.xml`** — added `cd_movie_backdrop` string for `ImageView` content description

---

## 2026-02-23 – Feature: Phase 7.1 – Movie Card Stack (SwipingActivity)

**What:** Implemented the movie card swipe deck in `SwipingActivity`. The host now sees rich full-screen movie cards when the session starts — each loaded from the TMDB trending endpoint.

**Files created/modified:**

- **`ui/swiping/MovieCardAdapter.java`** _(new)_
  - `RecyclerView.Adapter` for `ViewPager2`
  - Loads **backdrop** image via Glide (falls back to poster if null)
  - Displays title, `⭐ X.X` rating, 3-line overview
  - `setMovies(List<Movie>)` for live data updates

- **`ui/swiping/SwipingActivity.java`** _(updated)_
  - Fetches trending-day movies from TMDB on `onCreate` using `TmdbApiClient`
  - `CompositePageTransformer`: `MarginPageTransformer(24dp)` + custom scale (8%) + alpha (30%) for deck depth effect
  - Yes/No buttons call `advanceCard()` stub — vote logic comes in Phase 7.2
  - Exit session still clears `LobbyPrefs` before navigating home

---

## 2026-02-23 – Fix: "Tap to Return to Lobby" Banner Incorrectly Shown After Session Exit

**What:** The "Tap to return to lobby" banner was appearing on the Home screen after a user clicked "Exit Session" in `SwipingActivity`. This was wrong because once a session has started (host clicked "Start Swiping"), there is no lobby to return to — the user has intentionally left the session.

**Root cause:** `SwipingActivity` did not call `LobbyPrefs.clearActiveRoomCode()` when navigating away. The room code saved during the lobby waiting phase was still in SharedPreferences, so `HomeActivity.checkActiveLobby()` found it and showed the banner.

**Fix — `SwipingActivity.java`:**

- Added `LobbyPrefs.clearActiveRoomCode(this)` in `onCreate()` — clears the room code the moment swiping begins. This ensures the banner never appears even if the user presses Back (instead of using Exit button).
- Also added the same clear call inside the `btn_exit_session` click listener as a safety guard.

**Behaviour summary:**

- Banner **shows** → only while in the lobby waiting room (session not yet started). Users can press Back to browse the Home screen and tap the banner to jump back in.
- Banner **hidden** → as soon as `SwipingActivity` starts (host has clicked "Start Swiping"), the room code is cleared for all participants.

---

## 2026-02-23 – Feature: "Tap to Return to Lobby" Sticky Banner + Back-Press Fix

**What:** A primary-colored sticky banner is now shown at the top of the Home screen whenever the current user is still a member of an active lobby. Tapping it navigates back to `LobbyActivity` with their **live role** (host or member) read from Firebase — so re-joining as the wrong role is no longer possible.

**Root cause of role-override bug:** `CreateLobbyActivity.onBackPressed()` was calling `removeMember()`, which deleted the user's Firebase record. If the host pressed Back and then navigated back to the lobby, `joinLobby()` would write them as a plain member (not host). Similarly, other devices could not see them as a member anymore.

**Changes:**

- **`LobbyPrefs.java`** _(new)_ — Thin `SharedPreferences` helper with `saveActiveRoomCode()`, `getActiveRoomCode()`, and `clearActiveRoomCode()`. Persists across Back presses and app restarts. Stored in `lobby_prefs` file under key `active_room_code`.

- **`FirebaseRepository.java`**:
  - Added `MemberLoadCallback` interface (`onResult(LobbyMember member)` — null = not found).
  - Added `getMember(roomCode, userId, callback)` — single-read of `lobbies/{roomCode}/members/{userId}`. Used to verify membership and get the live `isHost` value on Home resume.

- **`CreateLobbyActivity.java`**:
  - **Removed `onBackPressed()` override** — Back no longer removes the user from Firebase. Only the Leave Lobby button triggers removal.
  - Saves room code to `LobbyPrefs` after `createLobby()` succeeds.
  - Clears `LobbyPrefs` in `leaveLobby()` (explicit leave only).

- **`LobbyActivity.java`**:
  - Saves room code to `LobbyPrefs` in `onCreate()` — covers all joiners.
  - Clears `LobbyPrefs` in `leaveLobby()` (explicit leave only).

- **`activity_home.xml`**:
  - The `NestedScrollView` is now wrapped in a `ConstraintLayout` (`content_wrapper`).
  - A `MaterialCardView` banner (`banner_return_lobby`) is pinned to the top, `visibility="gone"` by default.
  - `NestedScrollView` is constrained below the banner — content is never hidden by it.

- **`HomeActivity.java`**:
  - `applyEdgeToEdgeInsets` now targets `content_wrapper` instead of `container_home`.
  - Binds `bannerReturnLobby` view in `onCreate`.
  - `onResume()` calls `checkActiveLobby()`: reads SP → calls `getMember()` → if member exists: shows banner with live `isHost`; if null: clears SP and hides banner (handles being kicked by host).
  - Tapping banner → `LobbyActivity` with the correct `room_code` + `is_host`.

- **`strings.xml`** — Added `banner_return_lobby` = `"↩  Tap to return to lobby"`.

---

## 2026-02-23 – Fix: Lobby Screen Horizontal Padding Stripped by Edge-to-Edge Insets

**Root cause:** `BaseActivity.applyEdgeToEdgeInsets()` called `v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)`. `setPadding()` **replaces** all existing padding, and `systemBars.left` / `systemBars.right` are `0` on standard portrait phones — so the `android:padding="24dp"` defined in XML was silently wiped out on every physical device. The left/right sides of all lobby cards appeared flush with screen edges.

**Fix:**

- **`BaseActivity.java`** — `applyEdgeToEdgeInsets()` now converts `24dp → px` at runtime and adds it to the system bar insets on all four sides before calling `setPadding()`, preserving base padding on every device.
- **`activity_lobby.xml`** / **`activity_create_lobby.xml`** — Removed `android:padding="24dp"` from root `ConstraintLayout` (padding is now applied solely in `BaseActivity` to avoid double-padding).

---

## 2026-02-23 – Fix: Lobby Button Gap Grows as Members Leave

**Root cause:** Both lobby layouts used `ScrollView + fillViewport="true"`, which expanded the inner `ConstraintLayout` to fill the full screen height. The buttons were chained with `constraintTop_toBottomOf` the element above, so the gap between the members card and the Leave Lobby button stretched to fill all remaining screen space. Fewer members = taller gap. After a host transfer with only 1 member remaining, the gap was extreme.

**Fix:** Rewrote both lobby layouts to use a **full-height `ConstraintLayout`** (no `ScrollView`) with a **bottom-anchored button chain**:

- `card_leave_lobby` — pinned to `parent` bottom; never moves.
- `card_start_swiping` — `constraintBottom_toTopOf(card_leave_lobby)`, `visibility="gone"` for non-hosts. When GONE, it collapses upward; Leave Lobby stays put.
- `card_members` — `height="0dp"`, fills all space between `card_room_code` and the button stack.
- `RecyclerView` inside members card — `height="0dp" + layout_weight="1"`, scrolls internally when member count is high.

**Files changed:**

- **`activity_lobby.xml`** — Full rewrite to bottom-anchored `ConstraintLayout`.
- **`activity_create_lobby.xml`** — Same structural rewrite; `card_start_swiping` remains always-visible for the host.

---

## 2026-02-23 – Feature: Host Transfer on Leave + `createdBy` Lobby Field

**What:** When the host leaves a lobby that still has members, the host role is now transferred to the next remaining member. The original creator is also permanently recorded in Firebase.

**Root cause of silent failure:** The existing `removeMember()` host-transfer code was never firing. `createLobby()` and `joinLobby()` wrote the host flag with key `"host"` (matching the `LobbyMember.isHost()` Java-bean getter serialization), but `removeMember()` was reading and writing `"isHost"` — so the host check always returned `null`/`false` and the transfer block was silently skipped.

**Changes:**

- **`FirebaseRepository.java`**:
  - Added `createdBy: hostId` to lobby metadata in `createLobby()` — written once, never overwritten. Stores the original creator's **UID** (stable; names can change, UIDs cannot).
  - Fixed `removeMember()`: reads `"host"` (not `"isHost"`) to detect the leaving host; writes `"host": true` to promote the next candidate; updates `hostId` on the lobby root; logs the transfer.
  - Updated Javadoc schema comment to document `createdBy` and the correct `"host"` key name.

- **`LobbyActivity.java`** — `updateOrAddMember()` now detects when the current user's own record changes from member to host (`isMe && member.isHost() && !isHost`). On promotion: flips `isHost = true`, makes `card_start_swiping` visible, and wires the Start Swiping click listener — all without restarting the activity.

- **`strings.xml`** — Added `share_room_code_message` format string used by `LobbyActivity.shareRoomCode()`.

---

## 2026-02-23 – Fix: Empty Username in Lobby Members

**Root cause:** `CreateLobbyActivity` and `JoinLobbyActivity` were reading `FirebaseUser.getDisplayName()` to set the lobby member's username. For email/password users this is always `null`, resulting in an empty `username` field in Firebase.

**Fix:** Both activities now call `UserRepository.getUserProfile(uid)` on start to fetch the user's real `name` and `gender` from `/users/{uid}` in Realtime Database. The real values are stored in `currentUsername` / `currentGender` fields and passed to Firebase when creating or joining the lobby.

**Files changed:**

- **`LobbyMember.java`** — Added `gender` field (+ getter/setter) so it is persisted in Firebase alongside `username`.
- **`FirebaseRepository.java`** — `createLobby()` and `joinLobby()` now accept a `gender` parameter and write it into the member node.
- **`CreateLobbyActivity.java`** — Replaced `getDisplayName()` with `UserRepository.getUserProfile()` call in `onCreate()`; room code generation is deferred until the profile arrives. Passes `currentGender` to `createLobby()`.
- **`JoinLobbyActivity.java`** — Same pattern: profile is pre-fetched in `onCreate()`; `attemptJoin()` passes the resolved `currentGender` to `joinLobby()`.
- **`MemberAdapter.java`** — `MemberItem` now has a `gender` field. `onBindViewHolder` shows gender as the subtitle; falls back to "Host"/"Member" if gender is empty.
- **`LobbyActivity.java`** / **`CreateLobbyActivity.java`** — `updateOrAddMember()` now passes `member.getGender()` to the `MemberItem` constructor.

---

## 2026-02-23 – Lobby UI Refinements

**What:** Improved the visual design and usability of the Create Lobby and Join Lobby screens.

**Changes:**

- **`item_member.xml`** — Rebuilt member card to match the sidebar avatar style: 48×48dp rounded avatar (`default_user_avatar3.jpg`), username in bold, role subtitle ("Host" / "Member") below, host badge + online dot on the right.
- **`activity_lobby.xml`** — Members section header is now a horizontal row with "Members" on the left and a live `text_member_count` (e.g. "2 / 10") on the right. Buttons restyled: Start Swiping uses a solid primary card/button (50dp radius, matching home screen Create Lobby), Leave Lobby uses an outlined error-colour card/button.
- **`activity_create_lobby.xml`** — Added same live member count header. Added "Leave Lobby" outline button below Start Swiping so the host can leave even when alone in the lobby. Restyled Start Swiping to match the new solid primary pattern.
- **`MemberAdapter.java`** — Binds `image_avatar` (always `default_user_avatar3`), `text_role` ("Host" / "Member" from string resources), retains existing host badge and online dot logic.
- **`strings.xml`** — Added `label_member` string.
- **`LobbyActivity.java`** — Added `textMemberCount` and `cardStartSwiping` fields; `refreshAdapter()` now updates count; host-visibility toggle now applies to the whole card wrapper.
- **`CreateLobbyActivity.java`** — Added `textMemberCount` and `btnLeaveLobby` fields; `updateOrAddMember()` and `refreshAdapter()` update count; Leave Lobby button wired to `leaveLobby()`.

---

## 2026-02-22 – Phase 5: Lobby Management

**What:** Implemented the full lobby lifecycle: create, join, real-time member list, Start Swiping, and Leave/Disband — all backed by Firebase Realtime Database.

**New Files:**

- **`data/model/LobbyMember.java`** — POJO matching the Firebase schema (`username`, `joinedAt`, `isHost`). Required no-arg constructor for Firebase deserialization.
- **`data/repository/FirebaseRepository.java`** — Singleton wrapping `FirebaseDatabase`. Methods: `createLobby()`, `lobbyExists()`, `joinLobby()` (with capacity + status guards), `listenMembers()` (`ChildEventListener`), `listenLobbyStatus()` (`ValueEventListener`), `setLobbyStatus()`, `removeMember()` (with last-member cleanup and host-transfer), `detachListeners()`.
- **`utils/RoomCodeGenerator.java`** — Generates random 6-char uppercase alphanumeric codes and verifies uniqueness against Firebase; retries up to 5 times on collision.

**Modified Files:**

- **`ui/lobby/CreateLobbyActivity.java`** — Generates room code, calls `createLobby()`, attaches real-time member listener, enables "Start Swiping" when ≥ 2 members, share button fires `ACTION_SEND`, back/destroy triggers `removeMember()` + `detachListeners()`.
- **`ui/lobby/JoinLobbyActivity.java`** — Validates 6-char alphanumeric format, calls `joinLobby()` (guards: exists, not full, status = "waiting"), maps errors to friendly strings, navigates to `LobbyActivity` on success.
- **`ui/lobby/LobbyActivity.java`** — Receives `room_code` + `is_host` extras, shows real-time member list, host-only Start Swiping (enabled when ≥ 2 members), status listener auto-navigates non-host devices to `SwipingActivity`, Leave Lobby confirmation dialog with `removeMember()` cleanup.
- **`res/values/strings.xml`** — Added `msg_lobby_not_found`, `msg_lobby_full`, `msg_lobby_already_started`, `error_invalid_code`, `msg_generating_code`, `msg_leave_lobby_confirm`.

**Key Design Decisions:**

- `FirebaseRepository` is a **singleton** to avoid duplicate listener registrations across activity recreations.
- `removeMember()` handles three cases in one method: (a) last member → delete entire lobby, (b) host leaves + others remain → transfer `isHost` + `hostId`, (c) regular member leaves → simple node remove.
- `LobbyActivity` uses `LinkedHashMap<userId, MemberItem>` to maintain insertion order and O(1) member update/removal.
- Status listener (`listenLobbyStatus`) is the single source of truth for navigating all devices to SwipingActivity when the host taps Start Swiping.

---

## 2026-02-22 – Popular Movies Vertical List

**What:** Added a vertical "Popular" movies section on the Home screen below the Top Rated carousel. Each card shows a poster, title, popularity score, genre chips, and a formatted release date. The list is rotated so items 15–20 display first and items 1–14 are appended at the end.

**Changes:**

- **`res/layout/item_movie_popular.xml`** _(NEW)_ – Horizontal card layout: 120×170dp poster on the left; title (bold, 2 lines), ⭐ popularity row, genre `ChipGroup`, and 📅 release date row on the right via `ConstraintLayout`. Card background set to `color_background`, elevation 0.
- **`ui/home/PopularMovieAdapter.java`** _(NEW)_ – Adapter with Glide poster loading, `"%.1f"` popularity formatting, dynamic genre `Chip` creation (background `color_surface`, text `color_text_primary`, no close icon), and `formatReleaseDate()` helper (`"YYYY-MM-DD"` → `"MMM yyyy"`).
- **`data/model/Movie.java`** – Added `popularity` (`double`) field with `@SerializedName` and `getPopularity()` getter.
- **`res/layout/activity_home.xml`** – Added Popular header (`ConstraintLayout` with "Popular" label + "See more" capsule button styled like Join Lobby) and `rv_popular_movies` `RecyclerView` (`nestedScrollingEnabled="false"`).
- **`res/values/strings.xml`** – Added `label_popular_movies` and `btn_see_more` strings.
- **`ui/home/HomeActivity.java`** – Added `popularAdapter` field and `setupPopularMovies()` (Retrofit call to `/movie/popular` page 1). Result is rotated: items 15–20 first, items 1–14 appended at end.

**Bug Fixes (same session):**

- **`res/drawable/calendar_icon.xml`** – Added missing `android:width="24dp"` / `android:height="24dp"` on `<vector>` (runtime crash: _width > 0_ required).
- **`res/layout/item_movie_popular.xml`** – Removed invalid `style="@style/Widget.Material3.ImageView"` from poster `ImageView` (resource linking failure).
- **`data/model/Movie.java`** – Added missing `popularity` field that caused a compile-time `cannot find symbol getPopularity()` error in `PopularMovieAdapter`.

---

## 2026-02-21 – Top Rated Movies Carousel

**What:** Added a second horizontal carousel ("Top Rated Movies") below the Trending section on the Home screen. Cards are wider (240dp) and show the movie title and genre names below the poster.

**Changes:**

- **`res/layout/item_movie_top_rated.xml`** _(NEW)_ – 240dp-wide card with poster `ImageView`, title `TextView`, and genres `TextView`.
- **`ui/home/TopRatedMovieAdapter.java`** _(NEW)_ – Adapter with `getGenresAsString(List<Integer>)` helper that maps all 19 standard TMDB genre IDs to display names using a static `HashMap`.
- **`data/model/Movie.java`** – Added `genre_ids` field (`List<Integer>`) with `@SerializedName` and `getGenreIds()` getter.
- **`data/api/TmdbApiService.java`** – Added `getTopRatedMovies(language, page, bearerToken)` endpoint (`GET /movie/top_rated`).
- **`res/layout/activity_home.xml`** – Added Top Rated label `TextView` and `rv_top_rated_movies` `RecyclerView` below the Trending section.
- **`res/values/strings.xml`** – Added `label_top_rated_movies` string.
- **`ui/home/HomeActivity.java`** – Added `topRatedAdapter` field and `setupTopRatedMovies()` method (Retrofit call to `/movie/top_rated`); called from `onCreate()`.

---

## 2026-02-21 – TMDB Trending Movies API Integration

**What:** Wired up a live Retrofit call to TMDB `/trending/movie/day` so the Home screen carousel shows real movie data instead of a blank list.

**Changes:**

- **`data/model/MovieListResponse.java`** _(NEW)_ – Gson wrapper for TMDB list responses (`page`, `results`, `total_pages`, `total_results`).
- **`data/api/TmdbApiService.java`** _(NEW)_ – Retrofit interface with `getTrendingMovies(timeWindow, language, bearerToken)` and `getPopularMovies()` endpoints.
- **`data/api/TmdbApiClient.java`** _(NEW)_ – Singleton Retrofit builder using `Constants.TMDB_BASE_URL` and `GsonConverterFactory`.
- **`ui/home/HomeActivity.java`** – `setupTrendingMovies()` now enqueues a real async Retrofit call to `/trending/movie/day` with `BuildConfig.TMDB_READ_ACCESS_TOKEN`. On success, `trendingAdapter.setMovies()` is called with the results list. Shows a Toast on network failure.

**Notes:** Bearer auth header is set per-call from `BuildConfig.TMDB_READ_ACCESS_TOKEN` (stored in `local.properties`). `TrendingMovieAdapter.setMovies()` mutates the adapter's existing `ArrayList` and calls `notifyDataSetChanged()`.

---

## 2026-02-21 – Home Screen: Trending Movies Carousel

**What:** Added a horizontal "Trending Movies" RecyclerView carousel to the Home screen, and wired up the Glide image loading library.

**Changes:**

- **`app/build.gradle.kts`** – Added Glide 4.16.0 (`glide` + `compiler` annotationProcessor).
- **`data/model/Movie.java`** _(NEW)_ – TMDB movie model with `@SerializedName` Gson fields: `id`, `title`, `posterPath`, `backdropPath`, `overview`, `voteAverage`, `releaseDate`.
- **`res/layout/item_movie_trending.xml`** _(NEW)_ – Movie card (160×240dp, `cardCornerRadius=16dp`, dark slate bg). Poster `ImageView` fills the card (`centerCrop`). Gradient scrim overlay at bottom with two `ImageButton`s: `btn_favorite` (`heart_outline_icon`) and `btn_watchlist` (`add_shadow_outline.`).
- **`res/drawable/gradient_bottom_scrim.xml`** _(NEW)_ – Bottom-to-top gradient shape for card scrim.
- **`ui/home/TrendingMovieAdapter.java`** _(NEW)_ – `RecyclerView.Adapter<MovieViewHolder>` with Glide poster loading (`https://image.tmdb.org/t/p/w500/{poster_path}`) and Toast click handlers for favourite and watchlist overlay buttons.
- **`res/layout/activity_home.xml`** – Refactored root `ConstraintLayout` to `NestedScrollView > LinearLayout` to support scrolling. Added `text_trending_label` (`TextView`) and `rv_trending_movies` (`RecyclerView`, horizontal).
- **`ui/home/HomeActivity.java`** – Added `setupTrendingMovies()` method: creates horizontal `LinearLayoutManager`, instantiates `TrendingMovieAdapter` with empty list (TMDB fetch to be wired in Phase 6).
- **`res/values/strings.xml`** – Added `label_trending_movies`, `label_movie_poster`, `label_add_to_favorite`, `label_add_to_watchlist`.

**Notes:** RecyclerView starts empty — real TMDB data will be fetched in Phase 6 (TMDB API integration). The `add_shadow_outline.` drawable name includes the trailing dot as supplied by the user; ensure the file exists with that exact name.

---

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

- **activity_splash.xml** – ConstraintLayout with dark background, centered logo (app_logo_named.png), Material3 CircularProgressIndicator at bottom. IDs: container_splash, image_logo, progress_loading.
- **SplashActivity.java** – Extends BaseActivity, sets splash layout, applies edge-to-edge insets. Empty of navigation logic (per plan).
- **Logo** – Copied app_logo_named.png to drawable (best for dark theme: transparent, vibrant gradients).
- **Colors** – Added dark theme palette: color_background (#050509), color_surface (#171923), color_primary (#39E1C6), etc. (APP_FLOW).
- **Themes** – Switched to Theme.Material3.Dark.NoActionBar, applied dark palette.
- **Manifest** – SplashActivity set as launcher; MainActivity no longer launcher.
- **Strings** – app_name set to "CineMatch".

**Files created/updated:**

- `app/src/main/res/layout/activity_splash.xml` (new)
- `app/src/main/res/drawable/app_logo_named.png` (copied)
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

---

## 2025-02-20 – Onboarding flow and Home display name

**What:** After registration, users are sent to an Onboarding screen to fill required profile fields (name, gender, birthday) before reaching Home. Back/swipe from Onboarding is blocked. Home shows the user’s name from the saved profile.

**Changes:**

- **OnboardingActivity** (new):
  - Single screen with form: **Name** (TextInputLayout), **Sex/Gender** (Material dropdown: Male, Female, Other, Prefer not to say), **Birthday** (click opens DatePickerDialog; stored as `yyyy-MM-dd`, displayed e.g. `May 21, 1998`).
  - “Continue to Home” saves profile via `UserRepository.saveUserProfile()` then navigates to Home with clear task.
  - **Back disabled:** `OnBackPressedCallback` consumes back press and shows toast: “Please complete the form to continue.”
  - Validation: name required, gender required, birthday required; errors shown on fields and in error TextView.
- **RegistrationActivity:** After successful sign-up, navigates to **OnboardingActivity** (not Home) with `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`.
- **SplashActivity:** If user is authenticated, loads `users/{uid}` via `UserRepository.getUserProfile()`. If no profile or empty name → **OnboardingActivity**; otherwise → **HomeActivity**.
- **LoginActivity:** After successful sign-in, same profile check: no profile or empty name → **OnboardingActivity**; otherwise → **HomeActivity**.
- **HomeActivity:** Loads `UserProfile` for current user and sets **text_username** to `profile.getName()`. Falls back to auth email (or “Home”) if profile missing. **text_email** shows auth email. Avatar remains default drawable.
- **Layout:** `activity_onboarding.xml` – logo, title “Complete your profile”, subtitle, name/gender/birthday inputs, error TextView, “Continue to Home” button (dark theme, Material 3).
- **Strings:** Onboarding title, subtitle, labels, button, errors, gender options, back-press toast. **Manifest:** `OnboardingActivity` registered.

**Files created/updated:**

- `app/src/main/res/layout/activity_onboarding.xml` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/onboarding/OnboardingActivity.java` (new)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/RegistrationActivity.java` (updated)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/LoginActivity.java` (updated)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/splash/SplashActivity.java` (updated)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/HomeActivity.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)
- `app/src/main/AndroidManifest.xml` (updated)

**Notes:** User cannot leave Onboarding without submitting the form (back is intercepted). Profile is written to `users/{uid}` (name, gender, birthday, email). Home displays the saved name at the top; avatar stays the default (no Firebase Storage).

**Fix (logout):** Logout was only navigating to LoginActivity without signing out of Firebase Auth, so reopening the app showed the user still logged in. HomeActivity now calls `AuthRepository.getInstance().signOut()` before starting LoginActivity and finishing, so the Firebase session is cleared and the next app launch correctly shows the Login screen.

---

## 2026-02-21 – Home Screen Sidebar Navigation

**What:** Added a slide-in sidebar (DrawerLayout) to the Home screen, triggered by the hamburger menu button in the navbar.

**Changes:**

- **activity_home.xml** – Restructured root to `DrawerLayout`. Existing `ConstraintLayout` is the main content; sidebar (`layout_sidebar.xml`) included as `layout_gravity="end"` panel.
- **layout_sidebar.xml** (new) – Sidebar layout with:
  - User avatar (`default_user_avatar2`) in a `MaterialCardView` (56dp, 16dp corners) alongside `sidebar_text_username` and `sidebar_text_email` TextViews.
  - Nav buttons: **Home** (closes drawer), **About Project** (toast), **About Team** (toast).
  - **Logout** button at the bottom (red tint) that calls `AuthRepository.signOut()` and navigates to `LoginActivity`.
- **HomeActivity.java** – Wires `btn_menu` to open/close drawer; wires all sidebar buttons; loads `UserProfile` from `UserRepository` to populate sidebar username and email; adds double-back-press-to-exit (with drawer-close-first behavior).
- **strings.xml** – Added sidebar string resources: `sidebar_nav_home`, `sidebar_nav_about_project`, `sidebar_nav_about_team`, `sidebar_default_username`, `sidebar_about_project_coming_soon`, `sidebar_about_team_coming_soon`.

**Files created/updated:**

- `app/src/main/res/layout/layout_sidebar.xml` (new)
- `app/src/main/res/layout/activity_home.xml` (updated – DrawerLayout root)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/HomeActivity.java` (updated)
- `app/src/main/res/values/strings.xml` (updated)
