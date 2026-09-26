# 🌿 My Own Vocabulary

An intuitive, luxury offline-first Android vocabulary learning and retention application built with **Kotlin**, **Jetpack Compose (Material 3)**, **Room Database**, and **Native Text-To-Speech (TTS)**.

Organize your words into a structured hierarchy of **Main Folders** and **Sub-Folders**, configure language pairs (e.g., German ➔ English, Spanish ➔ English), practice with multi-format interactive quizzes, track daily learning streaks via an interactive calendar, and listen to authentic native pronunciation.

---

## ✨ Key Features

### 1. 🗂️ Hierarchical Vocabulary Organization
- **Main Folders & Sub-Folders**: Structure your learning by language level or topic (e.g., *German A1 / A2* ➔ *Common Nouns*, *Essential Verbs*; *Travel Spanish* ➔ *Airport & Hotel*).
- **Language Pairing**: Set source and target languages for every folder to enable automatic pronunciation and localized quizzes.
- **German Article / Gender Color Coding**:
  - `der` (Masculine) ➔ Sapphire Blue (`#3B82F6`)
  - `die` (Feminine) ➔ Vibrant Rose / Crimson (`#F43F5E`)
  - `das` (Neuter) ➔ Emerald Green (`#10B981`)
  - Other parts of speech (verbs, adjectives) ➔ Elegant tiered badges.

### 2. 🧠 Interactive Practice & Quizzes
- **Multiple Choice (4 Options)**: Test your recognition with real-time emerald (correct) and red (wrong) visual feedback, haptic response, and auto-pronunciation.
- **3D Flashcard Flip**: Interactive 3D flip card animation with self-rating ("Need Practice" vs. "Got It!").
- **Spelling & Typing Test**: Strengthen spelling accuracy with accent tolerance and instant verification.
- **Mistake Review**: Summary screen with accuracy percentages and a one-tap "Retry Mistakes" mode.

### 3. 📅 Interactive Daily Calendar & Learning Streak
- **Month Grid View**: Seamless month-by-month navigation with glowing emerald indicators showing the exact word count added each day (e.g., `+5`).
- **Daily Word Activity Log**: Select any calendar day to inspect words added on that date, toggle favorites, or listen to audio previews.
- **Streak Calculation**: Hero streak counter tracking consecutive days of learning.

### 4. 🔊 Authentic Text-To-Speech (TTS)
- Dedicated `TtsManager` wrapping native Android `android.speech.tts.TextToSpeech`.
- Automatic locale resolution for German (`Locale.GERMAN`), English (`Locale.ENGLISH`), French (`Locale.FRENCH`), Spanish (`Locale("es")`), and more.
- Audio focus management (ducks background audio gracefully).
- Customizable speech rate (0.5x to 1.5x) and voice pitch with an in-app voice test button.
- "Auto-Pronounce on Save" and "Auto-Pronounce in Quiz" settings.

### 5. 💎 Luxury Emerald Design System (Replicated from Money Tracker App)
- **Palette**: Soft Emerald (`#2E9C7E`), Deep Green (`#1B3B34`), Emerald Glow (`#3DBFA0`), Dark Surfaces (`#0A0F0D`, `#111A17`, `#182420`, `#1F2E29`), and crisp light surfaces.
- **Tactile Physics**: `bouncyClickable` spring animations on all cards, buttons, and navigation tabs.
- **Signature Bottom Bar**: Custom cradle cutout navigation bar with a floating center FAB (`+`) nestled in the cradle with an ambient breathing glow aura.
- **Typography**: Google Fonts "DM Sans" in Normal, Medium, SemiBold, Bold, and ExtraBold.
- **Glassmorphism**: `EmeraldGlassCard` with dynamic borders, shadows, and frosted translucent fills.

### 6. 🔒 Offline-First Persistence & Cloud Sync
- **Local Room Database**: Offline-first source of truth with Coroutines Flow for instant real-time updates.
- **DataStore Preferences**: Persists theme mode, speech rate, voice pitch, and audio preferences.
- **Firebase Firestore Backup**: Optional cloud backup to sync words and folders under `users/{userId}/vocabulary_entries` for logged-in users.
- **JSON Import / Export**: Complete data backup and sharing via JSON format.

---

## 🏗️ Architecture & Tech Stack

```
com.sahed.my_own_vocabulary
├── data
│   ├── local
│   │   ├── dao            # MainFolderDao, SubFolderDao, VocabularyEntryDao
│   │   ├── entity         # MainFolderEntity, SubFolderEntity, VocabularyEntryEntity
│   │   ├── AppDatabase.kt # Room database with starter data prepopulator
│   │   └── DatabasePrepopulator.kt
│   ├── preferences        # AppPreferences with Jetpack DataStore
│   └── repository         # VocabularyRepository, SyncRepository
├── ui
│   ├── components         # VocabBottomBar (Cradle cutout + FAB)
│   ├── designsystem
│   │   ├── components     # EmeraldGlassCard, bouncyClickable, GentleEntrance, StatCards
│   │   └── theme          # Color, Shape, Type (DM Sans), Theme (Dark/Light/System)
│   ├── navigation         # AppNavGraph (Compose Navigation Host)
│   └── screens
│       ├── vocabulary     # Vocabulary Explorer with search & article filters
│       ├── quiz           # Practice games: Multiple Choice, Flashcard, Spelling
│       ├── entry          # Center (+) ModalBottomSheet for adding words
│       ├── calendar       # Interactive month grid & daily activity log
│       └── settings       # Preferences, TTS sliders, Manage Folders & Sub-Folders
└── util
    ├── TtsManager.kt      # Native TTS audio focus & locale engine
    └── JsonBackupHelper.kt# JSON Export & Import
```

- **Framework**: Kotlin 2.x & Jetpack Compose (BOM 2025+, Material 3)
- **Architecture Pattern**: Clean Architecture + MVVM + Unidirectional Data Flow (UDF)
- **Local Database**: AndroidX Room 2.6.1 + KSP
- **Preferences**: AndroidX DataStore Preferences 1.1.3
- **Audio / Speech**: Android `TextToSpeech` + `AudioManager`
- **Cloud Backend**: Firebase Authentication & Cloud Firestore (`my-own-vocabulary-123`)

---

## 🚀 Getting Started & Build

### Prerequisites
- Android Studio Ladybug / Meerkat (or newer)
- JDK 17 or JDK 21+
- Android SDK Platform 36 (Minimum SDK: 26)

### Build via Gradle
```bash
# Clone the repository
git clone https://github.com/sahedalomsumit/my-own-vocabulary.git
cd my-own-vocabulary

# Compile debug build
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug
```

---

## 👨‍💻 Author

Built with luxury Emerald design and crafted by **Sahed**.