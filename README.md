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

## About the Team

Our squad is composed of five specialists, each ensuring the successful deployment of the CineMatch ecosystem.

| Role | Member | Key Responsibilities |
| :--- | :--- | :--- |
| **Project Manager & UI/UX Lead** | Francis V. Palma | - Overall project roadmap and stakeholder management.<br>- Visual identity and Material 3 design system alignment. |
| **Database & Cloud Architect** | Rensen Israel C. Dela Cruz | - Realtime Database schema design and data normalization.<br>- Performance tuning for cloud sync latency. |
| **Lead Android Programmer** | Jerome S. Avecilla | - Core logic development, API integration, and networking pipeline.<br>- Secure credential management and build deployment. |
| **Frontend Developer** | Mico Laurenz D. Oleriana | - XML layout construction and UI state management.<br>- Implementing transitions and navigation components. |
| **Quality Assurance (QA)** | Ralph Andre F. Giga | - Functional testing across multiple emulator configurations.<br>- Validating API response handling and error edge cases. |

---

*Developed for the Final Project in Mobile Application Development 2.*
