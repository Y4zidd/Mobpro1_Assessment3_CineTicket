# CineTicket - Development Task & AI Tracking Guide

## 🤖 AI Assistant Instructions (System Prompt)
To the AI assisting with this project:
1. **Work Sequentially:** Execute strictly **one task at a time**. Do not generate code for multiple tasks at once. Wait for the user to confirm before moving to the next task.
2. **Strict Warning & Error Avoidance:** For **EVERY** task, you must proactively write modern, clean code that avoids errors, warnings, deprecations, and memory leaks. 
3. **Standard Output Format per Task:** Whenever you provide code for a task, you MUST append these two sections at the bottom of your response:
   - 🔍 **Code Review & Error Prevention:** Explicitly explain how the provided code avoids potential warnings, recomposition issues (in Compose), or deprecations (Rubric 1a).
   - 🔔 **Git Commit Reminder:** Provide the exact terminal command to commit this specific task (Rubric 1b).
4. **State Management:** When a task and its commit are confirmed complete, mark the checkbox `[ ]` to `[x]` in your memory and proceed to the next prompt.

## 📌 Project Overview & Rubric Mapping
* **App Name:** CineTicket
* **Concept:** A personal movie diary where users can browse TMDB movies, and log their own movie tickets/reviews.
* **Tech Stack:** Kotlin, **Jetpack Compose**, MVVM, Retrofit, Room Database, **Coil (Compose Image Loading)**, Coroutines/Flow, Firebase Auth (Google Sign-In), **Navigation Compose**.
* **Environment:** **Android Studio Panda**, **minSdk 26**.

---

## 🚀 Phase 1: Setup & Base Architecture (Rubric 1a, 1b, Syarat 1-3)
- [x] **Task 1.1:** Initialize Android Studio project using **Android Studio Panda**. Select "Empty Compose Activity" (Kotlin).
- [x] **Task 1.2:** Setup `build.gradle` (app level). 
  - **CRITICAL:** Set `minSdk = 26`.
  - Add Compose dependencies, Retrofit, Room, ViewModel, Flow, Coil (`io.coil-kt:coil-compose`), Navigation Compose, Firebase Auth, Google Play Services Auth. Ensure no deprecated libraries.
- [x] **Task 1.3:** Setup project package structure (`ui`, `data`, `network`, `db`, `model`, `theme`).

## 🔐 Phase 2: Authentication & Profile (Rubric 2a, 2b)
- [x] **Task 2.1:** Implement Google Sign-In via Firebase Auth in a `LoginScreen` (Composable).
- [x] **Task 2.2:** Save user session securely (persistent login using DataStore/SharedPreferences). The user must stay logged in when the app is killed and restarted. Add a Logout functionality that clears the session and navigates back to `LoginScreen`.
- [x] **Task 2.3:** Create `ProfileScreen` (Composable). Fetch the Google account's Photo URL, Display Name, and Email. Display the photo strictly as a circle using `AsyncImage` with `.clip(CircleShape)` modifier.

## 🌐 Phase 3: Networking - Fetching Data (Rubric 2c, 2f)
- [x] **Task 3.1:** Setup Retrofit client, DTOs, and API Interfaces.
- [x] **Task 3.2:** Implement GET request to MockAPI to fetch the user's specific ticket list. The request MUST filter data based on the currently logged-in user's email or ID. Parse JSON and display images using Coil in a `LazyColumn`.
- [x] **Task 3.3:** Implement Global Loading Indicator using `CircularProgressIndicator` for all network requests.
- [x] **Task 3.4:** Implement error handling and UI state for when there is NO internet connection (show a Compose Snackbar or Error Screen).

## 📝 Phase 4: Posting & Deleting Data (Rubric 2d, 2e)
- [x] **Task 4.1:** Create UI (`AddTicketScreen`) to add a new "Ticket/Diary". Requires Compose `TextField` (Review/Title) and Image Upload (Camera/Gallery using ActivityResultContracts).
- [x] **Task 4.2:** Implement POST request to send text and image URL to MockAPI. The `LazyColumn` list on the home screen MUST auto-update immediately on success via StateFlow.
- [x] **Task 4.3:** Implement DELETE functionality on the user's ticket list.
- [x] **Task 4.4:** Add a Compose `AlertDialog` confirmation before deleting. The `LazyColumn` list MUST auto-update immediately on success.

## 💾 Phase 5: Offline-First & Full CRUD (Rubric 3a, 3b)
- [x] **Task 5.1:** Setup Room Database entities and DAOs for the user's movie tickets.
- [x] **Task 5.2:** Combine REST API (MockAPI) with Room Database. Fetch from API -> save to Room -> display from Room via Flow. The app must support offline-first viewing.
- [x] **Task 5.3:** Implement UPDATE (Edit) feature (`EditTicketScreen`). Allow users to edit their existing ticket's text/image, completing the full CRUD operations.

## 🧹 Phase 6: Final Polish & Code Quality (Rubric 1a)
- [x] **Task 6.1:** Run "Inspect Code" in Android Studio Panda. AI must review to eliminate unused imports, hardcoded strings, and ensure 100% warning-free Compose code.
- [x] **Task 6.2:** Final UI/UX check (Ensure dark mode/light mode compatibility in Compose Material 3).
