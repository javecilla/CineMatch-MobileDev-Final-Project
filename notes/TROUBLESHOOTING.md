## CineMatch TROUBLESHOOTING Guide

This guide lists **common issues** when working on CineMatch and how to fix them.

---

## 🧱 Build & Gradle Issues

### **Problem:** Gradle sync failed / project does not build

**Possible causes & fixes:**

- **Missing or invalid `sdk.dir`**
  - Open `local.properties` in the project root.
  - Ensure `sdk.dir` points to a valid Android SDK install.
  - Example (Windows):  
    `sdk.dir=C\:\\Users\\<username>\\AppData\\Local\\Android\\Sdk`

- **No internet / dependency resolution errors**
  - Check network connection.
  - Retry Gradle sync from Android Studio (`File → Sync Project with Gradle Files`).

- **Incorrect Gradle or plugin versions**
  - Do not manually downgrade/upgrade plugin versions unless required by the project.
  - Accept Android Studio’s recommended Gradle updates only if the whole team agrees.

---

## 🔐 TMDB API Issues

### **Problem:** HTTP 401 / 403 from TMDB

**Likely causes:**
- Invalid or expired **TMDB Read Access Token (v4)**.
- Missing `Authorization` header due to misconfigured `local.properties`.

**Fix:**
- Verify `TMDB_READ_ACCESS_TOKEN` and `TMDB_API_KEY` in `local.properties`.
- Ensure the token is copied exactly from TMDB (no extra quotes or spaces).
- Confirm that Retrofit is using `BuildConfig.TMDB_READ_ACCESS_TOKEN` to build the `Authorization: Bearer <token>` header.

---

### **Problem:** TMDB calls succeed, but images do not load

**Likely causes:**
- Incorrect image base URL.
- Null or empty `poster_path` / `backdrop_path`.

**Fix:**
- Use the correct base URLs (see `APP_FLOW.md`):
  - Poster: `https://image.tmdb.org/t/p/w500{poster_path}`
  - Backdrop: `https://image.tmdb.org/t/p/w1280{backdrop_path}`
- Add placeholder/error images via Glide/Picasso for null paths.

---

## 🔥 Firebase & Realtime Database Issues

### **Problem:** Cannot connect to Firebase / database reads fail

**Likely causes:**
- Incorrect `FB_ROUTE_INSTANCE_URL` in `local.properties`.
- Missing or misconfigured `google-services.json`.

**Fix:**
- Check `FB_ROUTE_INSTANCE_URL` and ensure it matches the full Realtime Database URL in Firebase Console (including region).
- Confirm `google-services.json` is located at `app/google-services.json`.
- Rebuild the project after adding `google-services.json`.

---

### **Problem:** Permission denied when reading/writing database

**Likely causes:**
- Firebase Realtime Database rules too strict for current test scenario.

**Fix:**
- For development, temporarily use more permissive rules (as allowed by your course) and then tighten later.
- Ensure authenticated users meet the conditions defined in the rules.

---

### **Problem:** Listeners keep firing or memory leaks occur

**Likely causes:**
- Firebase listeners not removed in lifecycle callbacks.

**Fix:**
- Track references to each listener and remove them:
  - In ViewModels → override `onCleared()` and detach listeners.
  - In Activities/Fragments → detach in `onStop()` / `onDestroyView()` as appropriate.

---

## 👤 Authentication Issues

### **Problem:** Firebase Authentication fails (email/password)

**Likely causes:**
- Email/Password sign-in not enabled in Firebase Console.
- Weak password (fails Firebase requirements).

**Fix:**
- Enable Email/Password in `Firebase Console → Authentication → Sign-in method`.
- Validate email format and enforce a minimum password length (e.g., 6+ characters) before calling Firebase APIs.

---

## 📱 UI & Navigation Issues

### **Problem:** New Activity doesn’t open or app crashes on navigation

**Likely causes:**
- Activity not declared in `AndroidManifest.xml`.
- Wrong package/class name in `Intent`.

**Fix:**
- Add the Activity to `app/src/main/AndroidManifest.xml` with the correct `android:name`.
- Check package and class names match the Java file and conventions (see `CONVENTIONS.md`).

---

### **Problem:** Layout does not match dark theme design

**Likely causes:**
- Hardcoded colors in layouts.
- Not using Material 3 theme or color roles.

**Fix:**
- Move colors into `colors.xml` and reference them via theme attributes.
- Align with the dark palette defined in `APP_FLOW.md`.

---

## 🔁 Real-time Swiping & Match Issues

### **Problem:** Votes don’t appear to sync across devices

**Likely causes:**
- Writing votes to the wrong Firebase path.
- Not attaching listeners on the correct lobby/movie node.

**Fix:**
- Verify your Firebase paths follow the schema from `APP_FLOW.md` (`lobbies/{roomCode}/movies/{movieId}/votes/{userId}`).
- Log the path and data in repositories when writing votes to confirm correctness.

---

### **Problem:** Match never triggers even when all users like the same movie

**Likely causes:**
- Match detection logic not checking *all* current members.
- Timing issue (match logic runs before all votes are written).

**Fix:**
- Ensure match detection uses the members list under the lobby and verifies that each current member has a `true` vote for the same movie.
- Re-run match detection after each vote write or on vote change events.

---

## 🧪 General Debugging Tips

- Use **Logcat** with clear tags (`CINEMATCH_API`, `CINEMATCH_FIREBASE`, `CINEMATCH_UI`, etc.).
- Log Firebase paths and payloads when diagnosing data issues.
- For agents: when fixing an issue, also **document the cause and fix** in `LOGS_CHANGES.md` for future reference.

---

_If you encounter a recurring issue that is not listed here, add a new section with the problem, cause, and fix so future developers and agents benefit._

