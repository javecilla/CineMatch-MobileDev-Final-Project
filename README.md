<p align="center">
  <img src="https://jerome-avecilla.infinityfreeapp.com/assets/mobiledev/hero3.png" alt="Logo">
</p>

# <p align="center">CineMatch: Real-time Collaborative Movie Discovery Engine</p>

This project is a high-fidelity mobile application developed for the **IT 308W | MOBILE APPLICATION DEVELOPMENT 2** course at Bulacan State University.

---

## About the App

**CineMatch** is an enterprise-grade collaborative engine designed to eliminate decision paralysis for movie nights. Using a real-time swiping ecosystem, users can sync with friends to find the perfect film.

The application leverages the **TMDB (The Movie Database) API** for a massive content catalog and **Firebase Realtime Database** to maintain low-latency synchronization between lobby members.

### How It Works

The app utilizes a frictionless **Swipe-to-Match** mechanic designed for modern user retention. You aren't just picking a movie; you are syncing interests in real-time.

* **The Lobby Ecosystem:** A host creates a room, generating a unique `Room Code`. Members join the session in real-time, creating a shared execution context.
* **The Swiping Logic:** To survive the night, you must swipe through trending movies. "Yes" swipes are recorded in the cloud, while "No" swipes are discarded.
* **The Match:** Once every member in the lobby selects "Yes" for the same title, a "Match Found" event triggers, securing the deployment of your movie night.

### Core Features

| **Feature** | **Description** |
| :--- | :--- |
| **Real-time Sync** | Powered by Firebase RTDB for sub-second latency in collaborative voting. |
| **TMDB Integration** | Dynamic fetching of the latest trending, popular, and top-rated global cinema. |
| **Secure Auth** | Robust user identification via Firebase Authentication. |
| **Material 3 UI** | Modern Android Material Design 3 architecture for a premium look and feel. |

---

*Developed for the Final Project in Mobile Application Development 2.*
