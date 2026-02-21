# CineMatch Application Flow & Architecture Guide

## 📱 Overview

**CineMatch** is a real-time collaborative movie discovery engine that eliminates decision paralysis for movie nights. The app uses a swipe-to-match mechanic where users sync with friends in real-time to find the perfect film.

---

## 🎯 Core Application Flow

### **User Journey**

```
1. Authentication (Firebase Auth)
   ↓
2. Home Screen
   ├─ Create Lobby (Host)
   └─ Join Lobby (Member)
   ↓
3. Lobby Screen
   ├─ Display Room Code
   ├─ Show Connected Members
   └─ Start Swiping Session
   ↓
4. Swiping Screen
   ├─ Display Movie Cards (from TMDB)
   ├─ Swipe Right = Yes (Liked)
   ├─ Swipe Left = No (Disliked)
   └─ Real-time Sync with Firebase
   ↓
5. Match Found Screen
   ├─ Display Matched Movie
   ├─ Show Match Details
   └─ Options: Watch Now / Find Another
```

---

## 🏗️ Application Architecture

### **Technology Stack**

| **Component**      | **Technology**                | **Purpose**                 |
| :----------------- | :---------------------------- | :-------------------------- |
| **UI Framework**   | Android Material Design 3     | Modern, premium UI/UX       |
| **Backend**        | Firebase Realtime Database    | Real-time synchronization   |
| **Authentication** | Firebase Authentication       | Secure user identification  |
| **Movie Data**     | TMDB API (The Movie Database) | Movie catalog and metadata  |
| **Networking**     | Retrofit 2 + OkHttp           | HTTP client for API calls   |
| **JSON Parsing**   | Gson                          | JSON to Java object mapping |
| **Language**       | Java 11                       | Core development language   |
| **Min SDK**        | API 26 (Android 8.0)          | Minimum Android version     |
| **Target SDK**     | API 36                        | Latest Android features     |

### **Architecture Pattern**

**Recommended: MVVM (Model-View-ViewModel)**

- **Model**: Data classes, API services, Firebase repositories
- **View**: XML layouts, Activities, Fragments
- **ViewModel**: Business logic, state management, LiveData/Flow

**Alternative Consideration**: MVP (Model-View-Presenter) for simpler implementation

---

## 🔄 Data Flow & Synchronization

### **Firebase Realtime Database Schema**

```
lobbies/
  └─ {roomCode}/
      ├─ hostId: "user123"
      ├─ createdAt: timestamp
      ├─ status: "waiting" | "swiping" | "matched"
      ├─ members/
      │   ├─ {userId1}/
      │   │   ├─ username: "John"
      │   │   ├─ joinedAt: timestamp
      │   │   └─ isHost: true
      │   └─ {userId2}/
      │       ├─ username: "Jane"
      │       ├─ joinedAt: timestamp
      │       └─ isHost: false
      ├─ movies/
      │   └─ {movieId}/
      │       ├─ tmdbId: 12345
      │       ├─ title: "Movie Title"
      │       ├─ posterPath: "/path.jpg"
      │       └─ votes/
      │           ├─ {userId1}: true  (liked)
      │           └─ {userId2}: true  (liked)
      └─ matchedMovie/
          ├─ tmdbId: 12345
          ├─ title: "Matched Movie"
          └─ matchedAt: timestamp
```

### **Real-time Sync Logic**

1. **Lobby Creation**: Host creates room → Firebase generates unique room code
2. **Member Join**: Member enters room code → Firebase adds to members node
3. **Swiping**: User swipes → Firebase updates votes node in real-time
4. **Match Detection**: Firebase listener checks if all members voted "Yes" for same movie
5. **Match Event**: When match found → Update status to "matched" → Notify all members

---

## 🎬 TMDB API Integration

### **Endpoints Used**

| **Endpoint**                    | **Purpose**          | **Parameters**          |
| :------------------------------ | :------------------- | :---------------------- | --- |
| `/movie/popular`                | Get popular movies   | `language=en-US&page=1` |
| `/trending/movie/{time_window}` | Get trending movies  | `time_window=day`       |
| `/movie/top_rated`              | Get top-rated movies | `language=en-US&page=1` |
| `/movie/{movie_id}`             | Get movie details    | `movie_id`              |
| `/movie/{movie_id}/images`      | Get movie posters    | `movie_id`              |     |

### **API Response Structure**

```json
{
    "page": 1,
    "results": [
        {
            "adult": false,
            "backdrop_path": "/7HKpc11uQfxnw0Y8tRUYn1fsKqE.jpg",
            "genre_ids": [
                878,
                28,
                53
            ],
            "id": 1236153,
            "original_language": "en",
            "original_title": "Mercy",
            "overview": "In the near future, a detective stands on trial accused of murdering his wife. He has ninety minutes to prove his innocence to the advanced AI Judge he once championed, before it determines his fate.",
            "popularity": 581.4114,
            "poster_path": "/pyok1kZJCfyuFapYXzHcy7BLlQa.jpg",
            "release_date": "2026-01-20",
            "title": "Mercy",
            "video": false,
            "vote_average": 6.836,
            "vote_count": 287
        },
        ...
    ],
    "total_pages": 55272,
    "total_results": 1105421
}
```

### **Image URLs**

- **Poster**: `https://image.tmdb.org/t/p/w500/{poster_path}`
- **Backdrop**: `https://image.tmdb.org/t/p/w1280/{backdrop_path}`

---

## 🎨 UI/UX Design Principles

### **Material Design 3 Guidelines**

1. **Color Scheme (Dark Theme)**: Match the attached CineMatch mockup
   - **Background**: Near-black / charcoal (`#050509`–`#101018`)
   - **Surface / Cards**: Dark slate (`#171923`–`#202331`) with subtle elevation shadows
   - **Primary Accent**: Vibrant teal / cyan (`#39E1C6`–`#4EF2D7`) for active states, primary buttons, and highlights
   - **Secondary Accent**: Warm yellow / amber (`#FFC857`–`#FFB300`) for ratings (⭐) and subtle emphasis
   - **Error / Negative**: Desaturated red (`#FF4B5C`) for destructive actions or error messages
   - **Text Primary**: High-contrast white (`#FFFFFF`) for titles and key labels
   - **Text Secondary**: Soft gray (`#A0A3B1`) for supporting text and metadata
   - **Bottom Navigation**: Translucent dark bar with outlined / filled icons using the primary accent on selection
2. **Typography**: Material 3 type scale (headline, body, label) with bold, uppercase app title (`CINEMATCH`) and clear hierarchy for headings vs. metadata
3. **Components**: Material 3 components (Cards, Buttons, Chips, etc.)
4. **Motion**: Smooth transitions and animations
5. **Accessibility**: Support for screen readers, high contrast

### **Key Screens**

1. **Splash Screen**: App logo, loading indicator
2. **Authentication Screen**: Email/password or Google Sign-In
3. **Home Screen**: Create/Join lobby buttons
4. **Lobby Screen**: Room code display, member list, start button
5. **Swiping Screen**: Movie card stack, swipe gestures, member status
6. **Match Screen**: Matched movie details, celebration animation

---

## 🔐 Security & Best Practices

### **Security**

1. **API Keys**: Stored in `local.properties`, injected via `BuildConfig`
2. **Firebase Rules**: Implement proper security rules for database access
3. **Authentication**: Use Firebase Auth for secure user management
4. **Network**: Use HTTPS only, validate API responses
5. **Input Validation**: Validate room codes, user inputs

### **Best Practices**

1. **Error Handling**:
   - Network errors → Show user-friendly messages
   - API failures → Retry mechanism with exponential backoff
   - Firebase errors → Handle connection issues gracefully

2. **Performance**:
   - Image loading → Use Glide/Picasso for caching
   - API calls → Implement pagination for movie lists
   - Firebase listeners → Detach when not needed to save bandwidth

3. **Code Organization**:
   - Package structure: `ui/`, `data/`, `model/`, `utils/`
   - Use ViewModels for business logic
   - Repository pattern for data access

4. **Testing**:
   - Unit tests for business logic
   - Instrumented tests for UI flows
   - Test API integration with mock responses

---

## 📦 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/finalprojectandroiddev2/
│   │   │   ├── ui/
│   │   │   │   ├── auth/
│   │   │   │   ├── home/
│   │   │   │   ├── lobby/
│   │   │   │   ├── swiping/
│   │   │   │   └── match/
│   │   │   ├── data/
│   │   │   │   ├── api/
│   │   │   │   ├── repository/
│   │   │   │   └── model/
│   │   │   ├── viewmodel/
│   │   │   └── utils/
│   │   ├── res/
│   │   │   ├── anim/ (for animations)
│   │   │   ├── layout/
│   │   │   ├── layout-land/ (for landscape orientation)
│   │   │   ├── values/
│   │   │   └── drawable/
│   │   └── AndroidManifest.xml
│   └── test/
└── build.gradle.kts
```

---

## 🚫 Constraints & Limitations

### **Technical Constraints**

1. **Min SDK 26**: No support for Android versions below 8.0
2. **Internet Required**: App requires active internet connection
3. **Firebase Limits**: Free tier has connection limits (100 concurrent)
4. **TMDB Rate Limits**: API has rate limiting (40 requests per 10 seconds)
5. **Device Storage**: Cache movie images, but limit cache size

### **Business Constraints**

1. **Room Size**: Limit lobby members (e.g., max 10 members)
2. **Session Timeout**: Auto-disconnect after inactivity (e.g., 30 minutes)
3. **Match Logic**: All members must vote "Yes" for match (strict requirement)

---

## 🔄 State Management

### **Key States**

1. **Authentication State**: Logged in / Logged out
2. **Lobby State**: Not in lobby / In lobby (waiting) / Swiping / Matched
3. **Swiping State**: Loading movies / Displaying card / Processing swipe
4. **Network State**: Online / Offline / Error

### **State Handling**

- Use **LiveData** or **StateFlow** for reactive state updates
- Handle configuration changes (screen rotation) properly
- Save state in ViewModel to survive activity recreation

---

## 🎯 Key Features Implementation

### **1. Swipe Gesture**

- Use **ViewPager2** or custom **GestureDetector**
- Implement card stack animation
- Detect swipe direction (left = No, right = Yes)

### **2. Real-time Updates**

- Use Firebase **ValueEventListener** for lobby updates
- Use Firebase **ChildEventListener** for member/vote changes
- Update UI reactively when data changes

### **3. Match Detection**

- Listen to votes node in Firebase
- Check if all members have voted "Yes" for same movie
- Trigger match event when condition met

### **4. Room Code Generation**

- Generate unique 6-character alphanumeric code
- Check Firebase for uniqueness
- Display code prominently for sharing

---

## 📱 Device Compatibility

- **Minimum**: Android 8.0 (API 26)
- **Target**: Android Latest (API 36)
- **Screen Sizes**: Support phones and tablets
- **Orientations**: Portrait (primary), Landscape (optional)

---

## 🧪 Testing Strategy

1. **Unit Tests**: Business logic, data models, utilities
2. **Integration Tests**: API calls, Firebase operations
3. **UI Tests**: Screen navigation, swipe gestures, user flows
4. **Manual Testing**: Multiple devices, network conditions, edge cases
5. **Change Logging**: After implementing or modifying any feature, **record the change in `notes/LOGS_CHANGES.md`** (what changed, who did it, and when). This log is the single source of truth so future agents and developers know which features are already done and avoid duplicating work.

---

## 📚 Additional Resources

- [TMDB API Documentation](https://developers.themoviedb.org/3)
- [Firebase Realtime Database Guide](https://firebase.google.com/docs/database/android/start)
- [Material Design 3](https://m3.material.io/)
- [Android Architecture Components](https://developer.android.com/topic/architecture)

---

_This document serves as the architectural guide for CineMatch development. Update as the project evolves._
