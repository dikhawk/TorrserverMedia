# 🎬 TorrServerMedia

**TorrServerMedia** is a cross-platform application for working with [TorrServer](https://github.com/YouROK/TorrServer), allowing you to easily stream media via torrent links.

---

## 🚀 **Project Features**

- 📌 Supports Android, Windows, Linux
- 📌 Intuitive user interface
- 📌 Direct integration with TorrServer
- 📌 Supports external players for media playback

---

## 🛠 **Technologies and Libraries**

The project is built with **Kotlin Multiplatform (KMP)** using modern tools:

### 🎯 Core Stack:

- [**Kotlin Multiplatform**](https://github.com/JetBrains/kotlin) — shared code for Android and Desktop (Windows, Linux)
- [**Jetpack Compose**](https://github.com/JetBrains/compose-multiplatform) — declarative UI for Android and Desktop
- [**Ktor**](https://github.com/ktorio/ktor) — for network requests
- [**Coroutines**](https://github.com/Kotlin/kotlinx.coroutines) — for asynchronous and multithreading support
- [**Decompose**](https://github.com/arkivanov/Decompose) — a Kotlin Multiplatform library for breaking down your code into tree-structured, lifecycle-aware business logic components (aka BLoC), with routing functionality and pluggable UI (Jetpack/Multiplatform Compose, Android Views, SwiftUI, Kotlin/React, etc.)
- [**TorrServer**](https://github.com/YouROK/TorrServer) — a simple and powerful tool for streaming torrents.

---

## 🛠️ **Setup and Run**

### 🔧 **Prerequisites**

1. **Install JDK 17 or later**
2. **Set up Android Studio**
3. **Add `local.properties` with the path to Android SDK**

### ▶️ **Running the project**

#### **Android**
To run the application on an Android device/emulator:
- Open the project in Android Studio and run the imported Android run configuration

To build the application bundle:
- Run `./gradlew :composeApp:assembleDebug`
- Find the `.apk` file at `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

Run Android simulator UI tests:
```bash
./gradlew :composeApp:pixel5Check