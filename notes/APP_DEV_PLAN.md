# CineMatch Development Implementation Plan

## 📋 Overview

This plan breaks down the CineMatch app development into manageable phases, following a **UI-first approach** followed by functionality implementation. Each phase builds upon the previous one.

**Required Activities (per course instructions):**

- **Registration Page** — New users create an account via Firebase Auth
- **Login Page** — Returning users sign in; **shown first when app opens** (user must login to access the app)
- **Main Page(s)** — Home, Lobby, Swiping, Match screens (accessed only after login)

**App entry flow:**  
Splash → **Login** (if not authenticated) | **Home** (Main) (if authenticated). From Login, user can navigate to Registration for new accounts.

**Development Strategy:**

1. **UI First** - Build all layouts and visual components
2. **Functionality Second** - Add logic, state management, and features
3. **Integration Third** - Connect APIs and Firebase
4. **Polish Last** - Add animations, error handling, and refinements

---

## 🎯 Phase 1: Project Setup & Foundation

**Goal:** Set up project structure, dependencies, and core utilities.

**Estimated Time:** 1-2 days

### **Step 1.1: Project Structure Setup**

**Tasks:**

- [x] Create package structure (`ui/`, `data/`, `model/`, `viewmodel/`, `utils/`)
- [x] Set up constants file (`Constants.java` or `Config.java`)
- [x] Create base classes (BaseActivity, BaseFragment if using fragments)
- [x] Set up logging utility class

**Files to Create:**

- `app/src/main/java/com/example/finalprojectandroiddev2/utils/Constants.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/utils/Logger.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/utils/Utils.java`

**Deliverable:** Organized project structure with utility classes

---

### **Step 1.2: Data Models**

**Tasks:**

- [x] Create Movie model class (from TMDB API response)
- [x] Create Lobby model class (Firebase structure)
- [x] Create User model class (Firebase Auth)
- [x] Create Vote model class (for swiping votes)

**Files to Create:**

- `app/src/main/java/com/example/finalprojectandroiddev2/model/Movie.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/model/Lobby.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/model/User.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/model/Vote.java`

**Deliverable:** Complete data model classes with proper annotations

---

## 🎨 Phase 2: UI Foundation & Layouts

**Goal:** Create all necessary XML layouts and UI components without any functionality.

**Estimated Time:** 3-4 days

### **Step 2.1: Splash Screen**

**Tasks:**

- [x] Create `activity_splash.xml` layout
- [x] Add app logo/image
- [x] Add loading indicator
- [x] Create SplashActivity.java (empty for now)

**Files to Create:**

- `app/src/main/res/layout/activity_splash.xml`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/splash/SplashActivity.java`

**Deliverable:** Splash screen layout ready

---

### **Step 2.2: Login Screen (required — shown first when app opens)**

**Tasks:**

- [x] Create `activity_login.xml` layout
- [x] Add email input field
- [x] Add password input field
- [x] Add "Sign In" button
- [x] Add "Create account" / "Register" link or button (navigates to RegistrationActivity)
- [x] Add error message TextView (initially hidden)
- [x] Style with Material 3 components and dark theme

**Files to Create:**

- `app/src/main/res/layout/activity_login.xml`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/LoginActivity.java`

**Deliverable:** Login page layout ready (entry point for unauthenticated users)

---

### **Step 2.3: Registration Screen (required)**

**Tasks:**

- [x] Create `activity_registration.xml` layout
- [x] Add email input field
- [x] Add password input field (with confirm-password if desired)
- [x] Add "Register" button
- [x] Add "Already have an account? Sign In" link (navigates back to LoginActivity)
- [x] Add error message TextView (initially hidden)
- [x] Style with Material 3 components and dark theme

**Files to Create:**

- `app/src/main/res/layout/activity_registration.xml`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/RegistrationActivity.java`

**Deliverable:** Registration page layout ready (for new user sign-up)

---

### **Step 2.4: Home Screen (Main Page)**

**Tasks:**

- [x] Create `activity_home.xml` layout
- [x] Add "Create Lobby" button (large, prominent)
- [x] Add "Join Lobby" button (large, prominent)
- [x] Add user profile section (username, avatar)
- [x] Add logout button
- [x] Style with Material 3 cards and buttons

**Files to Create:**

- `app/src/main/res/layout/activity_home.xml`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/HomeActivity.java`

**Deliverable:** Home screen with navigation options

---

### **Step 2.5: Create Lobby Dialog/Screen**

**Tasks:**

- [x] Create `dialog_create_lobby.xml` or `activity_create_lobby.xml`
- [x] Add room code display (large, copyable)
- [x] Add "Share Room Code" button
- [x] Add "Start Swiping" button (initially disabled)
- [x] Add member list section (empty initially)
- [x] Add "Waiting for members..." indicator

**Files to Create:**

- `app/src/main/res/layout/dialog_create_lobby.xml` OR `activity_create_lobby.xml`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/CreateLobbyActivity.java`

**Deliverable:** Create lobby UI ready

---

### **Step 2.6: Join Lobby Screen**

**Tasks:**

- [x] Create `activity_join_lobby.xml` layout
- [x] Add room code input field (6 characters)
- [x] Add "Join" button
- [x] Add "Back" button
- [x] Add error message TextView (for invalid codes)
- [x] Style input field with Material 3 TextInputLayout

**Files to Create:**

- `app/src/main/res/layout/activity_join_lobby.xml`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/JoinLobbyActivity.java`

**Deliverable:** Join lobby UI ready

---

### **Step 2.7: Lobby Screen (Waiting/Swiping)**

**Tasks:**

- [x] Create `activity_lobby.xml` layout
- [x] Add room code display at top
- [x] Add member list RecyclerView
- [x] Add member status indicators (online, voting, etc.)
- [x] Add "Start Swiping" button (host only, initially)
- [x] Add "Leave Lobby" button
- [x] Add progress indicator for loading

**Files to Create:**

- `app/src/main/res/layout/activity_lobby.xml`
- `app/src/main/res/layout/item_member.xml` (RecyclerView item)
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/LobbyActivity.java`

**Deliverable:** Lobby screen with member list

---

### **Step 2.8: Swiping Screen**

**Tasks:**

- [x] Create `activity_swiping.xml` layout
- [x] Add movie card container (ViewPager2 or custom stack)
- [x] Add movie poster ImageView
- [x] Add movie title TextView
- [x] Add movie overview TextView
- [x] Add movie rating TextView
- [x] Add swipe indicators (Yes/No buttons as fallback)
- [x] Add member status bar (showing who's swiping)
- [x] Add "Exit Session" button

**Files to Create:**

- `app/src/main/res/layout/activity_swiping.xml`
- `app/src/main/res/layout/item_movie_card.xml`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/swiping/SwipingActivity.java`

**Deliverable:** Swiping screen with movie card UI

---

### **Step 2.9: Match Found Screen**

**Tasks:**

- [x] Create `activity_match.xml` layout
- [x] Add celebration animation/view
- [x] Add matched movie poster (large)
- [x] Add matched movie title
- [x] Add matched movie details (overview, rating, release date)
- [x] Add "Watch Now" button (or external link)
- [x] Add "Find Another Match" button
- [x] Add "Leave Lobby" button

**Files to Create:**

- `app/src/main/res/layout/activity_match.xml`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/match/MatchActivity.java`

**Deliverable:** Match screen with celebration UI

---

## 🔧 Phase 3: Core Functionality - Login & Registration (required)

**Goal:** Implement Firebase Authentication flow with **Login** and **Registration** pages. Login is the first screen shown when the app opens (unauthenticated users must sign in before accessing Main/Home).

**Estimated Time:** 2-3 days

### **Step 3.1: Firebase Auth Setup**

**Tasks:**

- [x] Initialize Firebase Auth in Application class or MainActivity
- [x] Create AuthRepository class
- [x] Implement `signIn(email, password)` method
- [x] Implement `signUp(email, password)` method (creates account in Firebase)
- [x] Implement `signOut()` method
- [x] Handle auth state changes via Firebase AuthStateListener

**Files to Create/Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/data/repository/AuthRepository.java`

**Deliverable:** AuthRepository ready for Login and Registration

---

### **Step 3.2: Login Activity Logic**

**Tasks:**

- [x] Connect LoginActivity UI to AuthRepository
- [x] Handle email/password input validation
- [x] Call `AuthRepository.signIn()` on "Sign In" button click
- [x] Show loading state during sign-in
- [x] Display Firebase error messages (invalid credentials, etc.)
- [x] Navigate to HomeActivity on successful login
- [x] Add "Create account" / "Register" action → start RegistrationActivity

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/LoginActivity.java`

**Deliverable:** Functional Login page (entry point for unauthenticated users)

---

### **Step 3.3: Registration Activity Logic**

**Tasks:**

- [x] Connect RegistrationActivity UI to AuthRepository
- [x] Handle email/password input validation (e.g., min length, valid email)
- [x] Call `AuthRepository.signUp()` on "Register" button click (creates Firebase account)
- [x] Show loading state during sign-up
- [x] Display Firebase error messages (email already in use, weak password, etc.)
- [x] On successful registration → navigate to HomeActivity (or optionally auto sign-in first)
- [x] Add "Already have an account? Sign In" → finish and return to LoginActivity

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/auth/RegistrationActivity.java`

**Deliverable:** Functional Registration page (new user sign-up)

---

### **Step 3.4: Auth State Management & Entry Flow**

**Tasks:**

- [x] Set SplashActivity or MainActivity as launcher activity
- [x] On app launch, check Firebase auth state:
  - If **authenticated** → navigate to HomeActivity (Main)
  - If **not authenticated** → navigate to LoginActivity (user must login first)
- [x] Prevent back navigation from Login to Splash/Main (or handle exit confirmation)
- [x] Handle session persistence (Firebase handles this; ensure no auto-logout on app restart unless intended)

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/splash/SplashActivity.java`
- `app/src/main/AndroidManifest.xml` (ensure LoginActivity, RegistrationActivity declared; launcher intent)

**Deliverable:** App opens to Login first when not authenticated; Registration available for new users

---

## 🏠 Phase 4: Home & Navigation

**Goal:** Implement home screen and navigation flow.

**Estimated Time:** 1-2 days

### **Step 4.1: Home Activity Logic**

**Tasks:**

- [x] Display current user info
- [x] Handle "Create Lobby" button click → Navigate to CreateLobbyActivity
- [x] Handle "Join Lobby" button click → Navigate to JoinLobbyActivity
- [x] Implement logout functionality
- [x] Handle back button (exit app confirmation)

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/HomeActivity.java`

**Deliverable:** Functional home screen

---

## 🎮 Phase 5: Lobby Management

**Goal:** Implement lobby creation, joining, and management.

**Estimated Time:** 4-5 days

### **Step 5.1: Firebase Database Setup**

**Tasks:**

- [x] Create FirebaseRepository class
- [x] Initialize Firebase Realtime Database
- [x] Set up database reference helpers
- [x] Create database utility methods

**Files to Create:**

- `app/src/main/java/com/example/finalprojectandroiddev2/data/repository/FirebaseRepository.java`

**Deliverable:** Firebase database connection ready

---

### **Step 5.2: Room Code Generation**

**Tasks:**

- [x] Create RoomCodeGenerator utility class
- [x] Generate unique 6-character alphanumeric codes
- [x] Check Firebase for code uniqueness
- [x] Retry if code exists

**Files to Create:**

- `app/src/main/java/com/example/finalprojectandroiddev2/utils/RoomCodeGenerator.java`

**Deliverable:** Unique room code generation

---

### **Step 5.3: Create Lobby Functionality**

**Tasks:**

- [x] Generate room code on create
- [x] Create lobby node in Firebase
- [x] Add host as first member
- [x] Set lobby status to "waiting"
- [x] Display room code in UI
- [x] Implement "Share Room Code" functionality
- [x] Listen for member joins in real-time

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/CreateLobbyActivity.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/data/repository/FirebaseRepository.java`

**Deliverable:** Working lobby creation

---

### **Step 5.4: Join Lobby Functionality**

**Tasks:**

- [x] Validate room code format
- [x] Check if lobby exists in Firebase
- [x] Check if lobby is full
- [x] Add user to members list
- [x] Navigate to LobbyActivity
- [x] Handle invalid code errors

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/JoinLobbyActivity.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/data/repository/FirebaseRepository.java`

**Deliverable:** Working lobby joining

---

### **Step 5.5: Lobby Activity - Member List**

**Tasks:**

- [x] Set up RecyclerView adapter for members
- [x] Listen to Firebase members node
- [x] Update member list in real-time
- [x] Show member status (online, voting)
- [x] Display host indicator
- [x] Handle member leave events

**Files to Create/Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/MemberAdapter.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/LobbyActivity.java`

**Deliverable:** Real-time member list updates

---

### **Step 5.6: Start Swiping Session**

**Tasks:**

- [x] Enable "Start Swiping" button when members > 1
- [x] Update lobby status to "swiping"
- [x] Navigate all members to SwipingActivity
- [ ] Initialize movie list for session

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/LobbyActivity.java`

**Deliverable:** Session start functionality

---

### **Step 5.7: Leave Lobby**

**Tasks:**

- [x] Remove user from members list
- [x] If host leaves, transfer host or disband lobby
- [x] Clean up Firebase listeners
- [x] Navigate back to HomeActivity
- [x] Handle edge cases (last member, etc.)

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/lobby/LobbyActivity.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/data/repository/FirebaseRepository.java`

**Deliverable:** Proper lobby cleanup

---

## 🎬 Phase 6: TMDB API Integration ✅ COMPLETED

**Goal:** Integrate TMDB API for movie data.

**Status:** Fully implemented. Trending, Top-Rated, and Popular movie lists are live on the Home screen.

---

### **Step 6.1: Retrofit Setup** ✅ Done

**Tasks:**

- [x] Create TMDB API interface (Retrofit)
- [x] Define API endpoints (popular, trending, top-rated)
- [x] Set up Retrofit instance with base URL
- [x] Configure Bearer token auth header (passed per-call via `@Header`)
- [x] Test API connection

**Files Created:**

- `data/api/TmdbApiService.java` _(was `TmdbApi.java` in plan — renamed)_
- `data/api/TmdbApiClient.java` _(was `ApiClient.java` in plan — renamed)_

**Deliverable:** ✅ Working Retrofit setup

---

### **Step 6.2: API Response Models** ✅ Done

**Tasks:**

- [x] Create Movie model (id, title, overview, poster_path, vote_average, etc.)
- [x] Create MovieListResponse model (wraps paginated results list)
- [x] Add Gson annotations for JSON parsing
- [x] Test model deserialization

**Files Created:**

- `data/model/Movie.java`
- `data/model/MovieListResponse.java`

> Note: `MovieResponse` from the plan was not needed — `MovieListResponse` covers all API responses.

**Deliverable:** ✅ Complete API models

---

### **Step 6.3: Movie Data Fetching** ✅ Done

**Tasks:**

- [x] Implement fetchPopularMovies()
- [x] Implement fetchTrendingMovies()
- [x] Implement fetchTopRatedMovies()
- [x] Handle API errors (via Retrofit `onFailure` callback)
- [ ] ~~Implement caching~~ — skipped (optional, not needed for now)

> Note: A dedicated `MovieRepository.java` class was not created. All three API calls are made directly in `HomeActivity.java` using `TmdbApiClient` + `TmdbApiService`. Functionally equivalent.

**Deliverable:** ✅ Movie data fetching working

---

### **Step 6.4: Image Loading** ✅ Done

**Tasks:**

- [x] Add Glide dependency (`build.gradle.kts`)
- [x] Implement poster image loading (all 3 adapters use `Glide.with(context).load(url)`)
- [x] Add placeholder and error images
- [ ] ~~Create ImageLoader utility class~~ — skipped (Glide called inline per adapter, sufficient for now)
- [ ] ~~Implement backdrop image loading~~ — not yet needed (only posters used)

**Files using Glide:**

- `ui/home/TrendingMovieAdapter.java`
- `ui/home/TopRatedMovieAdapter.java`
- `ui/home/PopularMovieAdapter.java`

**Deliverable:** ✅ Movie poster loading working

---

## 👆 Phase 7: Swiping Functionality

**Goal:** Implement swipe-to-match mechanic.

**Estimated Time:** 5-6 days

### **Step 7.1: Movie Card Stack** ✅ Done

**Tasks:**

- [x] Set up ViewPager2 with `MovieCardAdapter`
- [x] Create `MovieCardAdapter` (ViewPager2 `RecyclerView.Adapter`)
- [x] Display movie data in cards (title, rating, overview)
- [x] Load movie backdrop/poster images via Glide
- [x] Implement card animations (`CompositePageTransformer` — scale + alpha + margin gap)

**Files Created/Modified:**

- `ui/swiping/MovieCardAdapter.java` _(new — ViewPager2 adapter)_
- `ui/swiping/SwipingActivity.java` _(updated — fetches trending movies, wires adapter)_

> Yes/No vote logic is stubbed (`advanceCard()`) — will be wired in Step 7.2.

**Deliverable:** ✅ Movie card stack display

---

### **Step 7.2: Swipe Gesture Detection** ✅ Done

**Tasks:**

- [x] Implement swipe left (No) detection
- [x] Implement swipe right (Yes) detection
- [x] Add visual feedback during swipe
- [x] Animate card removal
- [x] Load next card after swipe

**Files Created/Modified:**

- `res/drawable/bg_swipe_label_yes.xml` _(new — green bordered background for YES label)_
- `res/drawable/bg_swipe_label_no.xml` _(new — red bordered background for NO label)_
- `res/layout/item_movie_card.xml` _(updated — added YES/NO overlay TextViews)_
- `ui/swiping/MovieCardAdapter.java` _(rewritten — OnTouchListener, drag/rotate, flyOff, snapBack, SwipeCallback)_
- `ui/swiping/SwipingActivity.java` _(updated — registered SwipeCallback routing to handleYes/handleNo)_

> Swipe and Yes/No button both call the same `handleYes()`/`handleNo()` — unified for Step 7.3 vote recording.

**Deliverable:** ✅ Working swipe gestures

---

### **Step 7.3: Vote Recording** ✅ Done

**Tasks:**

- [x] Record "Yes" votes to Firebase
- [x] Discard "No" votes (don't save)
- [x] Update user's vote status in Firebase
- [x] Detect unanimous match and set lobby status to `"matched"`

**Firebase schema:**

```
lobbies/{roomCode}/votes/{movieId}/{userId} = true   ← Yes vote
lobbies/{roomCode}/matchedMovieId = "{movieId}"      ← set on match
```

No votes are intentionally not written — absence of a user entry means No.

**Files Created/Modified:**

- `data/repository/FirebaseRepository.java` _(updated — `VoteCallback` interface, `recordVote()`, `checkForMatch()`)_
- `ui/swiping/SwipingActivity.java` _(updated — real `handleYes()` with Firebase write, `handleNo()` no-write, `listenForMatch()` status listener, `navigateToMatch()`, `onDestroy()` cleanup)_

> All devices listen to `status` changes — when status becomes `"matched"` every device navigates to `MatchActivity` simultaneously.

**Deliverable:** ✅ Votes saved to Firebase

---

### **Step 7.4: Real-time Vote Sync** ✅ Done

**Tasks:**

- [x] Listen to votes node in Firebase
- [x] Display which members have voted
- [x] Show member voting status in UI
- [x] Update UI when votes change

**Files Created/Modified:**

- `data/repository/FirebaseRepository.java` _(updated — `VotesCallback`, `AllMembersCallback` interfaces; `loadAllMembers()`; `listenVotesForMovie()` with active listener tracking; `detachVotesListener()`)_
- `ui/swiping/SwipingActivity.java` _(updated — `loadMembersAndStartVoteSync()`, `attachVoteSyncForMovie()`, `updateVoteStatusBar()`, `onPageSelected` re-attach hook, race condition guard)_

> Status bar format: `James ✓  ·  You ✓  ·  2/3 voted`. Updates in real-time as members vote.

**Deliverable:** ✅ Real-time vote synchronization

---

### **Step 7.5: Movie Queue Management** ✅ Done

**Tasks:**

- [x] Fetch initial movie list (20-30 movies)
- [x] Load more movies when queue is low — host-triggered "Load More"
- [x] Prevent duplicate movies — `appendMovies()` deduplicates by movie ID
- [x] Handle API pagination — `currentPage` incremented per Load More

**Design:** Host-controlled Load More. When the deck runs out, the host sees a **Load More Movies** button; members see **"Waiting for host to load more movies…"** until the host acts. Firebase `currentPage` broadcasts the next page number to all members so every device appends the identical new movies.

**Files Created/Modified:**

- `utils/Constants.java` _(updated — `NODE_CURRENT_PAGE`)_
- `ui/swiping/MovieCardAdapter.java` _(updated — `appendMovies()` with ID-based dedup)_
- `res/layout/activity_swiping.xml` _(updated — end-of-deck overlay with `btn_load_more` + `text_waiting_host`)_
- `res/values/strings.xml` _(updated — `btn_load_more`, `label_end_of_deck_title/subtitle`, `label_waiting_host_load_more`)_
- `data/repository/FirebaseRepository.java` _(updated — `PageCallback`, `setCurrentPage()`, `listenCurrentPage()`, `detachPageListener()`)_
- `ui/swiping/SwipingActivity.java` _(updated — `isHost` detection, `showEndOfDeck()`, `loadMoreMovies()`, `listenForPageChanges()`)_

**Deliverable:** ✅ Continuous movie queue

---

## 🎯 Phase 8: Match Detection & Display

**Goal:** Detect matches and display matched movie.

**Estimated Time:** 3-4 days

### **Step 8.1: Match Detection Logic**

**Tasks:**

- [ ] Create MatchDetector utility class
- [ ] Listen to votes for each movie
- [ ] Check if all members voted "Yes" for same movie
- [ ] Trigger match event when condition met
- [ ] Handle edge cases (member leaves during voting)

**Files to Create:**

- `app/src/main/java/com/example/finalprojectandroiddev2/utils/MatchDetector.java`

**Deliverable:** Match detection working

---

### **Step 8.2: Match Event Handling**

**Tasks:**

- [ ] Update lobby status to "matched"
- [ ] Save matched movie to Firebase
- [ ] Notify all members of match
- [ ] Navigate to MatchActivity
- [ ] Stop swiping session

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/swiping/SwipingActivity.java`
- `app/src/main/java/com/example/finalprojectandroiddev2/data/repository/FirebaseRepository.java`

**Deliverable:** Match event triggers navigation

---

### **Step 8.3: Match Activity Implementation** ✅

**Tasks:**

- [x] Fetch matched movie details (`getMatchedMovieId()` from Firebase → `getMovieDetails()` from TMDB)
- [x] Display movie poster and details (Glide poster, title, ⭐ rating, release year, overview)
- [x] Add celebration animation/confetti (Lottie `confetti.json`, plays on `onCreate`)
- [x] Implement "Watch on TMDb" button (opens `https://www.themoviedb.org/movie/{id}` in browser)
- [x] Implement "Find Another Match" button (resets Firebase `currentPage=0` + `status="swiping"`, navigates back to `SwipingActivity`)
- [x] Handle "Leave Lobby" action (`finishAffinity()` → `HomeActivity`)

**Files Modified:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/match/MatchActivity.java` — full implementation
- `app/src/main/res/layout/activity_match.xml` — redesigned with Lottie overlay + detail cards
- `app/src/main/assets/confetti.json` _(NEW)_ — Lottie confetti animation
- `app/src/main/java/com/example/finalprojectandroiddev2/data/api/TmdbApiService.java` — added `getMovieDetails()` endpoint
- `app/build.gradle.kts` — added Lottie `6.4.1` dependency
- `app/src/main/res/values/strings.xml` — added `msg_match_subtitle`, updated `btn_watch_now` text

**Deliverable:** Complete match screen ✅

---

## 🎬 Phase 9: Movies Activity

**Goal:** Create a dedicated **Movies** screen that mirrors the movie sections already on the Home screen (Trending carousel, Top Rated carousel, Popular vertical list) and adds a movie search field at the top. As a prerequisite, extract the shared navbar + divider into a reusable XML include so future activities don't repeat the same markup.

**Estimated Time:** 2-3 days

---

### **Step 9.1: Reusable Navbar + Divider Include**

**Context:**
The navbar (logo + hamburger menu) and the horizontal divider below it are currently inline inside `activity_home.xml`. The Movies Activity also needs the same navbar. Rather than duplicating the XML again — and in every future activity — extract it into a standalone layout file that can be included anywhere.

**Tasks:**

- [x] Create `layout_navbar.xml` — contains the `ConstraintLayout` with `iv_navbar_brand` (logo) + `btn_menu` (hamburger icon `MaterialButton`) and the `View` divider (`android:id="@+id/divider_header"`) below it, exactly matching the existing markup in `activity_home.xml` (lines 66–110).
- [x] Update `activity_home.xml` — replace the inline navbar + divider block with `<include layout="@layout/layout_navbar" />`.
- [x] Verify `HomeActivity.java` still resolves `R.id.iv_navbar_brand`, `R.id.btn_menu`, and `R.id.divider_header` after the refactor (IDs remain visible through the include, no Java changes required).

**Files to Create/Modify:**

- `app/src/main/res/layout/layout_navbar.xml` _(NEW)_ — standalone navbar + divider component
- `app/src/main/res/layout/activity_home.xml` _(MODIFY)_ — swap inline block for `<include>`

**Design notes:**

- Keep all existing IDs (`iv_navbar_brand`, `btn_menu`, `divider_header`) unchanged so `HomeActivity.java` compiles with zero changes.
- The included layout's root width should be `match_parent`; height `wrap_content`.

**Deliverable:** Reusable navbar component; Home Activity unchanged visually and functionally.

---

### **Step 9.2: Movies Activity Layout (`activity_movies.xml`)**

**Context:**
The wireframe shows (top to bottom):

1. Navbar (logo + hamburger), reusing `layout_navbar.xml`.
2. Page title "Movies".
3. Search `TextInputLayout` (with `search_icon` as end-icon, placeholder "Type movie name...", red-border error state).
4. **Trending Movies** — section header row (label left, "See More" pill button right) + horizontal carousel RecyclerView.
5. **Top Rated Movies** — same header row pattern + horizontal carousel RecyclerView.
6. **Popular** — same header row pattern + vertical RecyclerView.

**Tasks:**

- [x] Create `activity_movies.xml`:
  - Root: `ConstraintLayout` (`android:id="@+id/container_movies"`, `background="@color/color_background"`) wrapping a `NestedScrollView` -> `LinearLayout` (vertical, `paddingBottom="32dp"`).
  - First child inside `LinearLayout`: `<include layout="@layout/layout_navbar" />` (Step 9.1).
  - Page title `TextView` (`android:id="@+id/text_movies_title"`, text="Movies", `textSize="26sp"`, `textStyle="bold"`, `textColor="@color/color_text_primary"`, `marginTop="24dp"`, `marginStart/End="18dp"`).
  - **Search field** — `TextInputLayout` (`id="input_search_movies"`, style `Widget.Material3.TextInputLayout.OutlinedBox`, `marginTop="16dp"`, `marginStart/End="18dp"`, `hint="@string/hint_search_movies"`):
    - `app:endIconMode="custom"` + `app:endIconDrawable="@drawable/search_icon"` (existing drawable `res/drawable/search_icon.xml`)
    - `app:endIconTint="@color/color_text_secondary"`
    - Inner `TextInputEditText` (`id="edit_search_movies"`, `inputType="text"`, `maxLines="1"`, `imeOptions="actionSearch"`)
  - Error `TextView` (`id="text_search_error"`, `visibility="gone"`, `textColor="@color/color_error"`, `textSize="12sp"`, `marginStart/End="18dp"`, `marginTop="4dp"`) — sits directly below the `TextInputLayout` outside of it, same pattern as `text_error` in `activity_login.xml`.
  - **Trending section header** — `ConstraintLayout` (`paddingStart/End="18dp"`, `marginTop="32dp"`):
    - `TextView` `id="text_trending_label"` (text `@string/label_trending_movies`, `textSize="22sp"`, `textStyle="bold"`, constrained start + top + bottom)
    - `MaterialButton` `id="btn_trending_see_more"` (style `Widget.Material3.Button.OutlinedButton`, text `@string/btn_see_more`, `textSize="12sp"`, `textColor="@color/color_primary"`, `cornerRadius="50dp"`, `strokeColor="@color/color_primary"`, `strokeWidth="2dp"`, `backgroundTint="@color/color_surface"`, constrained end + top + bottom) — exact same spec as `btn_popular_see_more` in `activity_home.xml`.
  - **Trending RecyclerView** (`id="rv_trending_movies_page"`, `layout_marginTop="12dp"`, `paddingStart="18dp"`, `paddingEnd="6dp"`, `clipToPadding="false"`, `orientation="horizontal"`, `layoutManager` LinearLayoutManager, `tools:listitem="@layout/item_movie_trending"`, `tools:itemCount="4"`).
  - **Top Rated section header** — identical `ConstraintLayout` pattern with `id="text_top_rated_label"`, `text="@string/label_top_rated_movies"`, and `id="btn_top_rated_see_more"`.
  - **Top Rated RecyclerView** (`id="rv_top_rated_movies_page"`, same horizontal spec, `tools:listitem="@layout/item_movie_top_rated"`).
  - **Popular section header** — identical `ConstraintLayout` with `id="text_popular_label_page"`, `text="@string/label_popular_movies"`, and `id="btn_popular_see_more_page"`.
  - **Popular RecyclerView** (`id="rv_popular_movies_page"`, `paddingStart/End="18dp"`, `clipToPadding="false"`, `nestedScrollingEnabled="false"`, vertical `LinearLayoutManager`, `tools:listitem="@layout/item_movie_popular"`, `tools:itemCount="5"`).

**Files to Create/Modify:**

- `app/src/main/res/layout/activity_movies.xml` _(NEW)_
- `app/src/main/res/values/strings.xml` _(MODIFY)_ — add string `hint_search_movies` = "Type movie name..."

**Design notes:**

- All three section header rows copy the structure of the Popular header in `activity_home.xml` (lines 237–273): a `ConstraintLayout` with the label `TextView` constrained to start and a pill `MaterialButton` constrained to end, both vertically centered via `constraintTop/Bottom_toTopOf/BottomOf="parent"`.
- The search field mirrors the password `TextInputLayout` in `activity_login.xml` (lines 83–102): same `OutlinedBox` style + end-icon approach, but uses `endIconMode="custom"` + `endIconDrawable="@drawable/search_icon"` instead of `password_toggle`.
- All item layouts (`item_movie_trending`, `item_movie_top_rated`, `item_movie_popular`) are **reused as-is** — no new item layouts needed.

**Deliverable:** Fully designed `activity_movies.xml` matching the wireframe.

---

### **Step 9.3: MoviesActivity Java Class**

**Context:**
Wire up `activity_movies.xml` with Java. The API call pattern is identical to `HomeActivity`'s three `setup*Movies()` methods — reuse the same adapters (`TrendingMovieAdapter`, `TopRatedMovieAdapter`, `PopularMovieAdapter`) and the same `TmdbApiService` calls. Search functionality and "See More" navigation are stubs for now (future phases).

**Tasks:**

- [x] Create `MoviesActivity.java` in package `ui.movies`:
  - Extends `BaseActivity`.
  - `setContentView(R.layout.activity_movies)` + `applyEdgeToEdgeInsets(R.id.container_movies)`.
  - Navbar: wire `btn_menu` as an **up/back** button — `btn_menu.setOnClickListener(v -> finish())` (no drawer on this screen).
  - Call `setupTrendingMovies()`, `setupTopRatedMovies()`, `setupPopularMovies()` from `onCreate()` — implementations mirror those in `HomeActivity.java` exactly, using `BuildConfig.TMDB_READ_ACCESS_TOKEN` for the Bearer header.
  - "See More" buttons (`btn_trending_see_more`, `btn_top_rated_see_more`, `btn_popular_see_more_page`): each shows `Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()` — placeholder until a future phase adds dedicated list screens.
  - Search field: attach `OnEditorActionListener` to `edit_search_movies` for `IME_ACTION_SEARCH` — for now show a `Toast("Search coming soon")`. Add `// TODO: Phase 10 — implement TMDB search API call` comment.
  - Register in `AndroidManifest.xml`.

- [x] Update `HomeActivity.java`:
  - Change `sidebar_btn_movies` click listener from `Toast` to `startActivity(new Intent(this, MoviesActivity.class))`.

- [x] Update `AndroidManifest.xml`:
  - Add `<activity android:name=".ui.movies.MoviesActivity" />`.

**Files to Create/Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/movies/MoviesActivity.java` _(NEW)_
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/HomeActivity.java` _(MODIFY)_ — sidebar Movies button navigates to `MoviesActivity`
- `app/src/main/AndroidManifest.xml` _(MODIFY)_ — register `MoviesActivity`

**Design notes:**

- Use `TrendingMovieAdapter`, `TopRatedMovieAdapter`, `PopularMovieAdapter` from `ui/home/` directly — no new adapter classes.
- Bearer token: `"Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN` (same as `HomeActivity`).
- `onFailure` -> `Toast.makeText(this, "Failed to load ...", Toast.LENGTH_SHORT).show()` (same pattern as Home).
- Search and "See More" are intentionally deferred — mark with `// TODO: Phase 10` comments.

**Deliverable:** Fully functional Movies Activity displaying all three movie sections from TMDB, navigable from the sidebar "Movies" item.

---

## 🔍 Phase 10: Search Result Activity

**Goal:** Create a dedicated **Search Results** screen that takes a query from the Movies Activity, calls the TMDB Search API, and displays the matching movies using the popular movie layout design. The screen will also include pagination controls to navigate through the result pages.

**Estimated Time:** 2-3 days

---

### **Step 10.1: Search Result Activity Layout (`activity_searched_movie_result.xml`)**

**Context:**
The layout will reuse the navbar and the popular movie item design, while introducing a unique header and pagination footer.

**Tasks:**

- [x] Create `activity_searched_movie_result.xml`:
  - Root: `ConstraintLayout` (`background="@color/color_background"`).
  - Include `<include layout="@layout/layout_navbar" />` at the top.
  - **Header Row** (Below Navbar):
    - Left side: Back Button (`MaterialButton` using style `Widget.Material3.Button.OutlinedButton`, `app:icon="@drawable/arrow_back_icon"`, no text, same colors/radius as "See More" button).
    - Right side: Title `TextView` (text="Search Result", `textSize="26sp"`, `textStyle="bold"`, `textColor="@color/color_text_primary"`).
  - **Context Text**: `TextView` (e.g., "28 result for query 'Avengers end game'", `textSize="14sp"`, `textColor="@color/color_text_secondary"`).
  - **Movie List**: `RecyclerView` (`id="rv_searched_movies"`, vertical `LinearLayoutManager`, `tools:listitem="@layout/item_movie_popular"`). The list uses the exact same design/layout as the Popular movies section but represents search results.
  - **Pagination Footer** (Bottom of screen):
    - Left side: Info `TextView` (e.g., "Showing 20 out of 91 results", `textSize="12sp"`, `textColor="@color/color_text_secondary"`).
    - Right side: Two `MaterialButton`s for Previous (`@drawable/pagination_chevron_left_icon`) and Next (`@drawable/pagination_chevron_right_icon`). Outlined style.

**Files to Create:**

- `app/src/main/res/layout/activity_searched_movie_result.xml` _(NEW)_

**Deliverable:** ✅

---

### **Step 10.2: Configure TMDB Search API**

**Context:**
Add the TMDB Search endpoint to `TmdbApiService.java` to fetch movies based on a user's text query.

**Tasks:**

- [x] Update `TmdbApiService.java`:
  - Add `@GET("search/movie")` method.
  - Parameters: `@Query("query") String query`, `@Query("include_adult") boolean includeAdult` (default false), `@Query("language") String language` (default "en-US"), `@Query("page") int page`, and `@Header("Authorization") String bearerToken`.
  - Return type: `Call<MovieListResponse>`.

**Deliverable:** ✅

---

### **Step 10.3: Search Result Activity Logic (`SearchedMovieResultActivity.java`)**

**Context:**
Implement the Java logic to handle incoming search queries, fetch data from the API, populate the RecyclerView, and handle pagination state.

**Tasks:**

- [x] Create `SearchedMovieResultActivity.java` extending `BaseActivity`:
  - Retrieve the search `query` string passed via `Intent` extras from `MoviesActivity`.
  - **State tracking**: Maintain `currentPage` (starts at 1), `totalPages`, and `totalResults`.
  - **UI wiring**:
    - Back button: calls `finish()`.
    - Setup `RecyclerView` with `PopularMovieAdapter` (reused).
  - **API Integration**: Create `performSearch(int page)` method that calls the new TMDB search endpoint.
    - On success: Update the adapter, update the Context Text (total results and query), update Pagination Info Text (e.g., "Showing 20 out of 91 results"), and toggle Next/Prev button enabled states (Prev disabled on page 1).
  - **Pagination Logic**:
    - Next button: Increment `currentPage`, call `performSearch(currentPage)`, disable if `currentPage == totalPages`.
    - Prev button: Decrement `currentPage`, call `performSearch(currentPage)`, disable if `currentPage == 1`.
- [x] Update `AndroidManifest.xml` to register `SearchedMovieResultActivity`.

**Files to Create/Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/movies/SearchedMovieResultActivity.java` _(NEW)_
- `app/src/main/AndroidManifest.xml` _(MODIFY)_

**Deliverable:** ✅

---

### **Step 10.4: Triggering Search from Movies Activity**

**Context:**
Wire the search input field in `MoviesActivity` to initiate the search and navigate to `SearchedMovieResultActivity`.

**Tasks:**

- [x] Update `MoviesActivity.java`:
  - Handle the search action on `edit_search_movies` when the user presses keyboard Enter (`IME_ACTION_SEARCH`).
  - Handle clicking the custom end icon in `input_search_movies` (`setEndIconOnClickListener`).
  - Validation: Ensure the query is not empty before searching. Display an error in `text_search_error` if empty.
  - Action: Create an `Intent`, put the search text as an extra (`EXTRA_QUERY`), and start `SearchedMovieResultActivity`.

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/movies/MoviesActivity.java` _(MODIFY)_

**Deliverable:** ✅ Fully functional Search Results screen with pagination, navigable directly from the search bar in the Movies Activity.

---

## Phase 11: Movie Category (Trending, Popular, Top Rated) Activity

**Goal:** Create a reusable Activity designed to load paginated grids or lists of specific movie categories. It acts as the destination when the user clicks "See More" on any row inside the `MoviesActivity`.

**Estimated Time:** 1-2 days

---

### **Step 11.1: Movie Category Layout (`activity_movie_category.xml`)**

**Context:**
The layout structure will heavily emulate `activity_searched_movie_result.xml` to keep the UI consistent, featuring a custom header, a scrollable list, and pagination controls at the bottom.

**Tasks:**

- [ ] Create `activity_movie_category.xml`:
  - Root: `ConstraintLayout`.
  - **Navbar**: Include `<include layout="@layout/layout_navbar" />` at the top.
  - **Header Row**:
    - Left side Back Button (`MaterialButton` with `keyboard_arrow_left` icon, 40dp).
    - Right side Title `TextView` (e.g., "Trending Movies", dynamic text).
  - **Movie List**: `RecyclerView` (`id="rv_category_movies"`, vertical `LinearLayoutManager`, `tools:listitem="@layout/item_movie_popular"`).
  - **Pagination Footer**:
    - Info `TextView` (e.g., "Showing 20 out of 10000 results").
    - Prev / Next `MaterialButton`s using the chevron SVG icons.

**Files to Create:**

- `app/src/main/res/layout/activity_movie_category.xml` _(NEW)_

---

### **Step 11.2: Movie Category Activity Logic (`MovieCategoryActivity.java`)**

**Context:**
Implement a flexible Java activity that determines which API endpoint to ping (Trending, Popular, or Top Rated) based on the Intent Extra passed from the previous screen.

**Tasks:**

- [ ] Create `MovieCategoryActivity.java` extending `BaseActivity`:
  - Use `EXTRA_CATEGORY` passed via Intent to determine the context ("TRENDING", "POPULAR", "TOP_RATED").
  - Dynamically set the Header Title `TextView` to match the category.
  - Setup the `RecyclerView` reusing the `PopularMovieAdapter`.
  - **API Integration**: Create a `loadMovies(int page)` method carrying a Switch/If-Else block executing `TmdbApiService` endpoints conditionally.
  - **State tracking**: Implement the robust pagination math (`min(currentPage * 20, totalResults)`) that was built during Phase 10.
  - Connect Prev / Next button listeners to increment/decrement `currentPage`.
- [ ] Register `MovieCategoryActivity` in `AndroidManifest.xml` with `singleTop` launch mode natively.

**Files to Create/Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/movies/MovieCategoryActivity.java` _(NEW)_
- `app/src/main/AndroidManifest.xml` _(MODIFY)_

---

### **Step 11.3: Wiring "See More" Navigation**

**Context:**
Hook up the entry points from within `MoviesActivity`.

**Tasks:**

- [ ] Update `MoviesActivity.java`:
  - Add click listeners to `btn_trending_see_more`, `btn_top_rated_see_more`, and `btn_popular_see_more_page`.
  - Launch an Intent pointing to `MovieCategoryActivity`.
  - Put `EXTRA_CATEGORY` identifier into the intent data payloads so the destination knows what to load.

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/movies/MoviesActivity.java` _(MODIFY)_

**Deliverable:** ⏳ A fully reusable Category screen capable of serving unlimited paginated results for Trending, Popular, and Top Rated movies.

---

## Phase 12: Movie Item Modal

**Goal:** Create a reusable Bottom Sheet Modal that appears when a user long-presses a movie item across all movie lists (Home, Movies, Category, Search Results). It will display quick details fetched from the TMDB API and action buttons for Watchlist and Favorites backed by Firebase.

### **Step 12.1: Modal Layout Design (`layout_movie_modal.xml`)**

**Context:** Design the bottom sheet layout to match the provided wireframe, utilizing a rounded top border and a clean, stacked action list.

**Tasks:**

- [x] Create `layout_movie_modal.xml`:
  - Root: `LinearLayout` (vertical) with a rounded top-left and top-right background (e.g., 24dp corner radius).
  - **Header:**
    - Right-aligned Close Button (`ImageView` or `ImageButton` with `@drawable/close_icon`).
    - Divider line (copy from `layout_navbar.xml`).
  - **Movie Quick Details:**
    - Replicate the UI mapping from `item_movie_popular.xml` (Poster, Title, Popularity, Genres, Release Date).
    - Another horizontal divider line below the movie details.
  - **Action Buttons (Stack Layout):**
    - "View Full Details" button (Icon: `@drawable/view_icon`).
    - "Add to Watchlist" button (Icon: `@drawable/add_item_outline`, toggles to solid state).
    - "Add to Favorites" button (Icon: `@drawable/heart_outline_icon`, toggles to `@drawable/heart_solid_icon`).
    - Button styles should match `btn_share_room_code` styled with `Widget.Material3.Button.TextButton` and `iconGravity="textStart"`.

**Files to Create:**

- `app/src/main/res/layout/layout_movie_modal.xml` _(NEW)_
- Background drawable for bottom sheet with rounded top corners if needed `bg_bottom_sheet.xml` _(NEW)_

---

### **Step 12.2: Modal Fragment Implementation (`MovieModalBottomSheet.java`)**

**Context:** Implement the `BottomSheetDialogFragment` logic to handle fetching movie details, displaying the UI, and managing click events.

**Tasks:**

- [x] Create `MovieModalBottomSheet.java` extending `BottomSheetDialogFragment`.
- [x] Override `onCreateView` to inflate `layout_movie_modal.xml`.
- [x] Override `onCreateDialog` to enforce `setCancelable(false)` and `setCanceledOnTouchOutside(false)` so it exclusively closes via the Close button.
- [x] Add `movie_id` as a required Fragment argument.
- [x] On open, fetch full movie details via Retrofit: `https://api.themoviedb.org/3/movie/{movie_id}`.
- [x] Bind response data to the Movie Quick Details UI.
- [x] Set a click listener on the Close button to trigger `dismiss()`.

**Files to Create:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/movies/MovieModalBottomSheet.java` _(NEW)_

---

### **Step 12.3: Firebase Integration for Favorites & Watchlist**

**Context:** Implement the backend logic to store and retrieve the user's saved movies under their `uid`. Only basic details are stored to prevent excessive TMDB API calls when viewing lists later on.

**Tasks:**

- [x] Update `FirebaseRepository.java` to include methods for managing libraries:
  - `addToFavorites(String uid, Movie movie)`
  - `removeFromFavorites(String uid, int movieId)`
  - `checkIfFavorite(String uid, int movieId, Callback)`
  - Mirror the exact same signature methods for Watchlist.
- [x] In `MovieModalBottomSheet.java`, fetch the current saved status (Favorite/Watchlist) when the modal opens to properly set the initial icons (Solid vs Outline).
- [x] Add click listeners to the Action buttons:
  - Add/Remove from Firebase dynamically based on the current toggle state.
  - Toggle the button icons instantly based on the updated state.
  - Show a `Toast` or `Snackbar` confirming the action ("Added to Favorites").

**Data Structure (Firebase):**

```
/libraries
  /favorites
    /{uid}
      /{movieId}
        - title: "Movie Name"
        - posterPath: "/image.jpg"
        - genres: ["Action", "Crime"] (List/Array)
        - addedAt: timestamp

  /watchlist
    /{uid}
      /{movieId}
        - title: "Movie Name"
        - posterPath: "/image.jpg"
        - genres: ["Action", "Crime"] (List/Array)
        - addedAt: timestamp
```

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/data/repository/FirebaseRepository.java` _(MODIFY)_
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/movies/MovieModalBottomSheet.java` _(MODIFY)_

---

### **Step 12.4: Wiring Long-Press Triggers**

**Context:** Enable the modal to appear when movie items are long-pressed across every relevant list in the app.

**Tasks:**

- [x] Update `PopularMovieAdapter.java` (used consistently across almost all views):
  - Add an interface `OnMovieLongClickListener`.
  - Trigger the interface via `holder.itemView.setOnLongClickListener()`.
- [x] Implement the `OnMovieLongClickListener` in `HomeActivity`, `MovieCategoryActivity`, `MoviesActivity`, and `SearchedMovieResultActivity`.
- [x] In the callback of those activities, instantiate `MovieModalBottomSheet`, put the `movieId` into the parameters, and invoke `show()`.

**Files to Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/PopularMovieAdapter.java` _(MODIFY)_
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/HomeActivity.java` _(MODIFY)_
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/movies/MovieCategoryActivity.java` _(MODIFY)_
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/movies/MoviesActivity.java` _(MODIFY)_
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/movies/SearchedMovieResultActivity.java` _(MODIFY)_

---

## Phase 13: Library Activity

**Goal:** Create a Library screen for users to view their saved Watchlist and Favorite movies.

### **Step 13.1: Library Movie Item Layout (`item_movie_library.xml`)**

**Tasks:**

- [x] Create `item_movie_library.xml` for displaying a single saved movie.
- [x] Base the layout on `item_movie_top_rated.xml` but optimize for a vertical list constraint.
- [x] Make the movie poster/thumbnail wider (e.g., using a 16:9 backdrop style).
- [x] Display Movie Title and Genres text.

**Files to Create:**

- `app/src/main/res/layout/item_movie_library.xml` _(NEW)_

---

### **Step 13.2: Library Activity Layout (`activity_library.xml`)**

**Tasks:**

- [x] Create `activity_library.xml`.
- [x] Include the reusable navbar (`layout_navbar.xml`).
- [x] Add a screen title `TextView` with the text "Library".
- [x] Add a custom tab selector at the top (Watchlist | Favorites).
  - Use `LinearLayout` or custom tab views.
  - Active tab should have `@color/color_primary` text and a line indicator underneath.
- [x] Add an Info Message `TextView` below the tabs.
- [x] Add a `RecyclerView` configured with a vertical `LinearLayoutManager` to display the movie items.

**Files to Create/Modify:**

- `app/src/main/res/layout/activity_library.xml` _(NEW)_
- `app/src/main/res/values/strings.xml` _(MODIFY)_

---

### **Step 13.3: Library Adapter (`LibraryMovieAdapter.java`)**

**Tasks:**

- [x] Create `LibraryMovieAdapter` extending `RecyclerView.Adapter`.
- [x] Inflate `item_movie_library.xml` and bind movie data (Title, Genres, Glide image).
- [x] Implement `OnMovieLongClickListener` interface so long-pressing a movie card passes the event to the Activity.

**Files to Create:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/library/LibraryMovieAdapter.java` _(NEW)_

---

### **Step 13.4: Library Activity Logic (`LibraryActivity.java`)**

**Tasks:**

- [x] Create `LibraryActivity.java`.
- [x] Implement Tab click listeners to switch between Watchlist and Favorites:
  - Update tab styling (active color + line indicator).
  - Update Info Message text based on active tab.
  - Fetch corresponding list from `FirebaseRepository` and update the adapter.
- [x] Initialize `LibraryMovieAdapter` with an `OnMovieLongClickListener` that instantiates and shows `MovieModalBottomSheet(movieId)`.
- [x] Wire up the Sidebar "Library" button in `HomeActivity` to navigate to `LibraryActivity`.

**Files to Create/Modify:**

- `app/src/main/java/com/example/finalprojectandroiddev2/ui/library/LibraryActivity.java` _(NEW)_
- `app/src/main/AndroidManifest.xml` _(MODIFY)_
- `app/src/main/java/com/example/finalprojectandroiddev2/ui/home/HomeActivity.java` _(MODIFY)_
