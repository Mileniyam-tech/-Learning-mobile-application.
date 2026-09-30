# Learning Dashboard — Senior Android Engineering Assignment

A modern, offline-first Android application built with **Kotlin**, **Jetpack Compose**, **Coroutines / Flow**, and **Room Database**, adhering to **Clean Architecture** and **MVVM** principles.

---

### 1. Architecture: Why did you choose your architecture?
The application is structured according to **Clean Architecture + MVVM (Model-View-ViewModel)** with a unidirectional data flow:
* **Presentation Layer (Jetpack Compose + ViewModel + StateFlow):** Fully declarative UI observing immutable UI state (`Loading`, `Success`, `Error`, `Empty`). UI components are decoupled from data acquisition logic.
* **Domain Layer (Pure Kotlin Use Cases & Models):** Encapsulates core business rules (`CalculateCourseProgressUseCase`, `LoginUseCase`, `ToggleLessonCompletionUseCase`). Having explicit use cases allows independent unit testing without Android platform dependencies.
* **Data Layer (Offline-First Repository + Room + Mock API):** Follows the **Single Source of Truth (SSOT)** pattern. The UI consumes reactive streams (`Flow`) directly from Room database DAOs. The network layer populates and synchronizes the local database.

---

### 2. Offline Support: How are you storing and loading offline data?
* **Storage:** Data is persisted in a local SQLite database using **Android Jetpack Room** across relational tables: `courses` and `lessons` with foreign key cascades and indexing.
* **Loading & Sync Strategy:**
  1. The Repository immediately returns a `Flow` observing the Room database.
  2. If network is available or fresh sync is triggered, the Repository queries the API in the background on `Dispatchers.IO` and updates Room (`insertCourses`, `insertOrUpdateLessons`).
  3. When offline or on network failure, cached records in Room are seamlessly emitted to the UI, guaranteeing full offline functionality for both the Course Dashboard and Course Details screens.
  4. Local lesson state updates (`toggleLessonCompletion`) recalculate the course progress percentage and update Room atomically, keeping offline state reactive and persistent.

---

### 3. Security: Where would you store authentication tokens in a production application?
In a production Android application, authentication tokens (JWTs, refresh tokens, API keys) must never be stored in plaintext `SharedPreferences`. We use:
1. **EncryptedSharedPreferences / Jetpack Security (`androidx.security.crypto`):** Keys and values are encrypted using AES-256 (GCM / SIV) with keys managed by the **Android Keystore System**.
2. **Android Keystore Hardware Security Module (HSM / StrongBox):** Generates and stores cryptographic keys inside hardware-backed storage where key material never enters app process memory.
3. **BiometricPrompt Integration:** For sensitive workflows, decrypting access keys can be gated by user biometric authentication (Fingerprint / Face Unlock).
4. **Token Lifecycle Management:** Short-lived access tokens kept in-memory + refresh tokens in encrypted storage with an `Authenticator` / OkHttp interceptor for silent token rotation and auto-logout on 401.

---

### 4. Scale: If this application had 1M users + hundreds of courses, 3–5 things to improve:
1. **Paging 3 & Incremental Sync:** Implement `RemoteMediator` with Jetpack Paging 3 to paginate course catalogs from API into Room, streaming only visible chunks into memory.
2. **Background Sync Engine via WorkManager:** Schedule periodic, constraints-aware background sync (e.g., WiFi + charging) using `WorkManager` with exponential backoff and conflict resolution (last-write-wins or CRDTs).
3. **Optimistic UI Updates & Delta Synchronization:** When marking lessons completed offline, queue mutation actions locally and sync only changed deltas (`PATCH /sync/progress`) once connectivity is restored.
4. **Content Delivery Network (CDN) & Edge Caching:** Distribute course metadata, media assets, and lesson materials across global CDN edge nodes with HTTP caching headers (`ETag`, `Cache-Control`).
5. **Feature-Based Modularization & Architecture Splitting:** Modularize the codebase into `:core:database`, `:core:network`, `:feature:auth`, `:feature:dashboard`, and `:feature:course-detail` for parallel compilation, dynamic delivery, and team scalability.

---

### 5. Second Platform: Implementing on iOS/macOS
If implementing this solution for **Apple platforms (iOS / macOS)**:
* **UI & Presentation:** Build with **SwiftUI** using `@Observable` ViewModels (or `@StateObject` / `ObservableObject`) for reactive declarative state and SF Symbols for iconography.
* **Architecture:** Adopt **MVVM + Clean Architecture** or **The Composable Architecture (TCA)** for predictable state mutations and unidirectional data flow.
* **Data & Local Persistence:** Use **SwiftData** (or **Core Data** / **GRDB.swift**) for local SQLite persistence with `@Query` / NSFetchedResultsController providing reactive updates to the UI.
* **Concurrency & Networking:** Leverage modern **Swift Concurrency** (`async`/`await`, `AsyncSequence`, and `Task`) with native `URLSession` or `Alamofire` decoding via `Decodable`.
* **Security & Tokens:** Store authentication tokens securely in the **iOS Keychain Services API** (using `kSecAccessControlBiometryAny` or wrappers like `KeychainAccess` / `Valet`).
* **Unit Testing:** Implement unit tests using **XCTest** and `Swift Testing` framework with protocol mocks to verify progress calculation, state transitions, and offline repository fallback.
