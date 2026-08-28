<div align="center">
  <h1>📖 Quran App</h1>
  <p><strong>A Comprehensive Islamic Application: Quran, Tafseer, Azkar, Prayer Times & Qibla</strong></p>
  
  <p>
    <a href="https://flutter.dev"><img src="https://img.shields.io/badge/Flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white" alt="Flutter" /></a>
    <a href="https://dart.dev"><img src="https://img.shields.io/badge/Dart-0175C2?style=for-the-badge&logo=dart&logoColor=white" alt="Dart" /></a>
    <a href="#"><img src="https://img.shields.io/badge/GetX-State_Management-FF6F00?style=for-the-badge" alt="GetX" /></a>
  </p>

  <p>
    <a href="README_AR.md">AR عرض باللغة العربية</a>
  </p>
</div>

---

## 📖 Project Overview

**Quran App** is a fully-featured, production-ready Islamic application built with Flutter. It is designed to be the daily companion for Muslims, offering a seamless, ad-free, and fully offline experience. The app aggregates all essential Islamic tools into a single, beautifully crafted mobile interface.

From reading the Holy Quran with Uthmanic typography to accurate Prayer Times calculation, Azkar tracking, and a smart Electronic Sebha, the app uses modern Flutter patterns and **GetX** for robust state management and routing.

---

## ✨ Deep Dive into Features

### 📖 The Holy Quran & Tafseer (Exegesis)
- **Offline Reading:** The Quran text and Tafseer are bundled locally (via JSON assets), ensuring lightning-fast loads and offline availability.
- **Uthmanic Typography:** Beautifully rendered using custom fonts (`UthmanicHafs1.otf`) for an authentic reading experience.
- **Smart Bookmarking:** Automatically remembers the last read Surah and Ayah.
- **Integrated Tafseer:** Access simplified Tafseer (exegesis) seamlessly while reading, aiding in contemplation and understanding.

### 🕋 Prayer Times & Qibla Compass
- **High-Accuracy Adhan:** Uses `adhan_dart` alongside `geolocator` to calculate precise prayer times based on the user's exact coordinates.
- **Adhan Notifications:** Local background notifications utilizing `flutter_local_notifications` and `timezone` ensure users never miss a prayer.
- **Qibla Compass:** A smooth, responsive compass powered by `flutter_compass` that calculates the exact bearing to Mecca relative to the device's orientation.

### 📿 Smart Electronic Sebha (Tasbeeh)
- **Haptic Feedback Integration:** Provides subtle physical feedback (`HapticFeedback.lightImpact`) on every tap, and distinct vibrations upon completing a cycle (e.g., 33 times).
- **Cycle Tracking:** Not only counts the current Tasbeeh but keeps a historical tally of total completed loops, persisted securely via `shared_preferences`.

### 🤲 Azkar & Hadith Collections
- **Categorized Azkar:** Morning, Evening, Sleep, and Prayer Azkar properly grouped with interactive counters.
- **Hadith Library:** Access to key Hadith collections, presented in a highly readable list format.
- **Daily Reminders:** Scheduled local notifications to remind users to read their daily Azkar.

### 🎨 Modern UI & Theming
- **Dynamic Theming:** Seamless transition between Light and Dark modes managed by GetX, respecting system preferences.
- **Rich Animations:** Engaging user experience powered by `lottie` animations and custom path clippers (`flutter_custom_clippers`).
- **Responsive Layouts:** Utilizes `staggered_grid_view_flutter` for beautiful, masonry-style layouts in the Home and Features screens.

---

## ⚡ Tech Stack & Libraries

- **State Management & Routing:** `GetX` (Reactive state, dependency injection, and clean navigation).
- **Local Storage:** `shared_preferences` (For caching user settings, themes, and reading progress).
- **Data Source:** Local JSON Parsing (Eliminates the need for API calls for core features).
- **Location Services:** `geolocator`, `location` (Fetching coordinates safely with `permission_handler`).
- **Sensors:** `flutter_compass` (Magnetometer/Accelerometer sensor fusion for the Qibla).
- **Time & Notifications:** `flutter_local_notifications`, `flutter_native_timezone_latest`, `timezone`, `adhan`.

---

## 🏗️ Architecture & Code Structure

The project strictly follows a **Feature-First Clean Architecture**. This ensures that the codebase is highly scalable, modular, and easy to maintain.

```text
lib/
├── core/                     # Shared utilities, services, and core logic
│   ├── middleware/           # Route guards (e.g., AuthMiddleWare for Onboarding)
│   ├── service/              # Core services (SettingsServices, Notifications, Theming)
│   └── util/                 # Constants, Bindings, and GetX Routes definitions
├── features/                 # Modular feature directories
│   ├── adhan/                # Prayer times calculation and display
│   ├── azkar/                # Daily remembrances and tracking
│   ├── hadith/               # Hadith data parsing and UI
│   ├── home/                 # Main dashboard and navigation hub
│   ├── nameOfAllah/          # 99 Names of Allah explorer
│   ├── notifications/        # Scheduled alerts and Adhan overlays
│   ├── onboarding/           # First-time user experience screens
│   ├── pngtree/              # The Electronic Sebha (Tasbeeh) logic and UI
│   ├── qiblah/               # Compass sensor data and rendering
│   ├── quran/                # Quran reading, pagination, and font rendering
│   ├── settings/             # App preferences (Theme, Notification toggles)
│   └── tafsser/              # Tafseer logic and UI
└── main.dart                 # App entry point and global error handling
```

---

## 🚀 Getting Started

### Prerequisites
- [Flutter SDK](https://flutter.dev/docs/get-started/install) (Version 3.7.2 or higher).
- Android Studio, VS Code, or IntelliJ IDEA.
- A physical device or emulator (Physical device recommended for Compass/Qibla accuracy).

### Installation Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yousefm2311/Quran_App.git
   cd Quran_App
   ```

2. **Fetch dependencies:**
   ```bash
   flutter pub get
   ```

3. **Run the app:**
   ```bash
   flutter run
   ```

---

## 📱 Screenshots

*(Visual previews of the app's interfaces)*

![Quran App Preview](https://github.com/user-attachments/assets/6267444b-a13c-4a31-a7fd-6ed99743fccc)
![Home and Features](https://github.com/user-attachments/assets/f799112a-a9ed-4bcd-b12a-c155f552a70d)
![Azkar Section](https://github.com/user-attachments/assets/a1f5f88c-d744-4f9d-8ed7-555a15b0a24e)
![Qibla and Prayers](https://github.com/user-attachments/assets/ad8a3d76-93f2-4f82-bd1e-3751e9829633)

---

## 🛡️ Author

Developed with ❤️ by **Yousef Mohamed**
- **GitHub:** [yousefm2311](https://github.com/yousefm2311)
