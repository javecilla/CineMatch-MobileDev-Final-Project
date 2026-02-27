<p align="center">
  <img src="https://jerome-avecilla.infinityfreeapp.com/assets/mobiledev/mockup1.jpg" alt="Logo">
</p>

# <p align="center">CineMatch: Real-time Collaborative Movie Discovery Engine</p>

This project is a high-fidelity mobile application developed for the **IT 308W | MOBILE APPLICATION DEVELOPMENT 2** course at Bulacan State University.

---

## About the App

**CineMatch** is an enterprise-grade collaborative engine designed to eliminate decision paralysis for movie nights. Using a real-time swiping ecosystem, users can sync with friends to find the perfect film.

The application leverages the **TMDB (The Movie Database) API** for a massive content catalog and **Firebase Realtime Database** to maintain low-latency synchronization between lobby members.

### How It Works

The app utilizes a frictionless **Swipe-to-Match** mechanic designed for modern user retention. You aren't just picking a movie; you are syncing interests in real-time.

- **The Lobby Ecosystem:** A host creates a room, generating a unique `Room Code`. Members join the session in real-time, creating a shared execution context.
- **The Swiping Logic:** To survive the night, you must swipe through trending movies. "Yes" swipes are recorded in the cloud, while "No" swipes are discarded.
- **The Match:** Once every member in the lobby selects "Yes" for the same title, a "Match Found" event triggers, securing the deployment of your movie night.

### Key Features

- **Movie Discovery:**
  - Dynamic display of movie lists fetched from TMDB.
  - Search functionality to find movies by title.
  - Advanced filtering to browse movies by genres (Action, Drama, Horror, etc.).
  - Interactive carousels showcasing trending and top-rated movies.
- **User Personalization:**
  - Secure user registration and login powered by Firebase Authentication.
  - Ability to save movies to a personal Favorites list.
  - Capability to add movies to a Watchlist for future viewing.
- **Collaborative Swiping (The CineMatch Engine):**
  - Real-time lobby ecosystem where users can host or join movie-matching sessions.
  - Sub-second, low-latency synchronization of active lobby members using Firebase Realtime Database.
  - Frictionless "Swipe-to-Match" voting mechanic to collectively decide on the perfect film.

---

## Technologies Used

This project is built using modern Android technologies to ensure a responsive, efficient, and collaborative application.

- **Core Language:** [Java 11](https://www.oracle.com/java/)
- **Architecture:** MVVM Design Pattern & [Android Material Design 3](https://m3.material.io/)
- **Backend as a Service (BaaS):** [Firebase](https://firebase.google.com/)
  - **Authentication:** Firebase Authentication for secure user registration and login.
  - **Database:** Firebase Realtime Database for sub-second, low-latency sync of lobbies, members, and votes.
- **Networking & API:**
  - **[Retrofit 2](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/):** For HTTP client API calls.
  - **[TMDB API](https://developer.themoviedb.org/docs):** The Movie Database API for fetching dynamic movie catalogs, caching, and metadata.
  - **[Gson](https://github.com/google/gson):** For JSON to Java object mapping.
- **Media & Animation:**
  - **[Glide](https://github.com/bumptech/glide):** For efficient image loading and caching of movie posters.
  - **[Lottie](https://airbnb.io/lottie/):** For rendering JSON-based animations (e.g., match celebration).

---

## Project Resources

This section contains our project resources used to aid in development and design.

- **[Wireframe Designs](https://excalidraw.com/#room=5c0c26c0ecb8ff4e4727,NFSer29AAl41qPj9B_p8sA)**: Link to the Excalidraw file containing low-fidelity wireframe designs.
- **[Error Logs for QA](https://docs.google.com/spreadsheets/d/1EUGHnK1aJGAP2NVL3JZvdHEJib1k_ru_cL4ePVGh26c/edit?usp=sharing)**: Google Sheets containing error logs and issue tracking for Quality Assurance.
- **[Project Assets & Documentation](https://drive.google.com/drive/folders/1ZubPgTYm7fYUJA-5KPPIxT1hue9emqRw?usp=drive_link)**: Google Drive folder containing UI/UX assets, video ads, and other project-related documents.

---

_Developed for the Final Project in Mobile Application Development 2._
