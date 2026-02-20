## CineMatch GLOSSARY

Key terms used in the CineMatch project so everyone (developers and agents) speaks the same language.

---

### **CineMatch**
The Android application being built in this project: a **real-time collaborative movie discovery engine** using swiping and lobby-based sessions to find a movie everyone likes.

---

### **Lobby**
A virtual room where a group of users join together to run a shared movie discovery session. One user is the **Host**, others are **Members**. Backed by a `lobbies/{roomCode}` node in Firebase.

---

### **Room Code**
A short, unique alphanumeric string (e.g., 6 characters) that identifies a lobby. The host shares this code so other users can join the same session.

---

### **Host**
The user who creates a lobby. Typically responsible for:
- Generating the room code.
- Starting the swiping session.
- Potentially ending or resetting the session.

Represented in Firebase as a lobby member with `isHost = true`.

---

### **Member**
Any user (including the host) who has joined a lobby. Members see the same set of movies and their swipes contribute to the match logic.

---

### **Swipe**
The primary interaction gesture on a movie card:
- **Swipe Right / Yes** – The user likes the movie; recorded as a positive vote in Firebase.
- **Swipe Left / No** – The user does not like the movie; typically not stored (or stored as a negative decision depending on implementation).

Also available as Yes/No buttons as a fallback to gestures.

---

### **Match / Match Found**
The event that occurs when **every active member in a lobby** has swiped **Yes** on the same movie. Triggers:
- A `matchedMovie` entry under the lobby in Firebase.
- Navigation to a dedicated **Match screen** in the app showing details of the agreed-upon movie.

---

### **Swiping Session**
The active phase in a lobby where users are swiping through a stack/list of movies fetched from TMDB. The lobby’s status is typically set to `\"swiping\"` during this phase.

---

### **Match Screen**
The UI screen shown after a match is found. Displays:
- Poster and details of the matched movie.
- Optional actions like “Watch Now” (external) or “Find Another Match”.

---

### **Trending / Popular / Top Rated Movies**
Categories of movies provided by TMDB:
- **Trending** – Movies currently trending over a time window (e.g., day).
- **Popular** – Widely watched movies at the moment.
- **Top Rated** – Movies with the highest ratings.

These may be used as sources for the movie stack in swiping sessions.

---

### **TMDB (The Movie Database)**
The external API providing:
- Movie metadata (title, overview, release date, rating, etc.).
- Poster and backdrop images.

Integration is handled via Retrofit and uses a **Read Access Token (v4)** for authorization.

---

### **Firebase Realtime Database**
The cloud database providing **real-time synchronization** between lobby members. Used to store:
- Lobbies and their status.
- Members and their roles (host/member).
- Movies considered in a session.
- Votes and matched movie information.

---

### **Firebase Authentication**
Authentication service used to:
- Register and log in users (e.g., via email/password).
- Associate lobby membership and votes with specific user IDs.

---

### **Swipe-to-Match Mechanic**
The core interaction design where users swipe through movies and a **match** triggers only when **all members** in a lobby have swiped **Yes** on the same title.

---

### **Activity**
An Android screen in the app (e.g., `AuthActivity`, `HomeActivity`, `LobbyActivity`, `SwipingActivity`, `MatchActivity`). Each corresponds to an XML layout (e.g., `activity_auth.xml`).

---

### **ViewModel**
Part of the MVVM architecture. Holds UI state and business logic for a given screen, interacts with repositories, and exposes reactive data (e.g., LiveData/StateFlow) to the UI layer.

---

### **Repository**
Abstraction layer that coordinates data from:
- TMDB API (via Retrofit).
- Firebase Realtime Database.
- Any local caching (if added).

Examples: `AuthRepository`, `MovieRepository`, `FirebaseRepository`.

---

### **Card Stack**
The UI layout used on the Swiping screen to show movie cards (using ViewPager2 or a custom implementation). Users swipe cards left/right to vote.

---

### **Session Timeout**
Business rule that automatically ends a lobby or swiping session after a period of inactivity (e.g., 30 minutes), to avoid stale sessions lingering in Firebase.

---

_If you introduce new domain concepts (e.g., watchlists, seasons, genres filtering), add them to this glossary so future contributors and agents share the same mental model._

