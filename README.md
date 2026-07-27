# FunHouse Multiplatform 🕹️

Welcome to **FunHouse Multiplatform**, a premium Kotlin Multiplatform (KMP) rewrite of the classic **Gepetto Games Collection** (originally FunHouse Legacy). 

This project unifies a massive library of retro text adventures, classic board/card games, and interactive chatbots into a single, modern codebase that compiles for multiple platforms using Jetpack Compose Multiplatform.

---

## 📱 Supported Target Platforms
* **Android**: Optimized application layout matching Material 3 Adaptive Navigation standards.
* **Desktop**: Fully packaged native apps for **macOS** (`.dmg`, `.pkg`) and **Windows** (`.msi`, `.exe`).
* **Web (Wasm-JS)**: High-performance canvas-rendered browser target.

---

## 🎮 Game Directory & Architecture

The codebase is modularized using distinct feature modules located under `feature/`:

### 1. Game Engines & Adventure Collections
* **FunHouse Engine** (`:feature:funhouse-engine-kotlin`): Core CSV-driven multiplayer game engine powering:
  * *Island*
  * *FunHouse*
  * *Space Station Aegis*
* **Wander Engine** (`:feature:wander-engine-kotlin`): Drive-driven logic engine powering:
  * *Wander Castle*
  * *Wander Aldebaran*
  * *Wander Library*
  * *Wander Logic Ops*

### 2. Migrated Classic Text Adventures
These games have been migrated from C/Fortran to clean, coroutine-based Kotlin:
* **Colossal Cave Adventure** (`:feature:colossal-cave-adventure-kotlin`)
* **Dinkum** (`:feature:dinkum-kotlin`)
* **Mystery Mansion** (`:feature:mistery-mansion-kotlin`)
* **Secret Forest** (`:feature:secret-forest-kotlin`)
* **Castle** (`:feature:castle-kotlin`)
* **Chimaera** (`:feature:chimaera-kotlin`)
* **Hangman** (`:feature:hangman-kotlin`)
* **Orb Of Zot** (`:feature:wizards-castle-kotlin`)

### 3. Casino & Chance Games
* **Blackjack** (`:feature:blackjack`)
* **Chess** (`:feature:chess`)
* **Craps** (`:feature:craps`)
* **Poker** (`:feature:poker`)
* **Roulette** (`:feature:roulette`)
* **Slot Machine** (`:feature:slot-machine`)

### 4. Interactive Chatbots & Arcades
* **Eliza** (`:feature:eliza-kotlin`): The classic conversational therapist chatbot.
* **Classic Arcades** (`:feature:classic-arcades`): Retro-inspired casual arcade modules.
  - Paddle Ball
  - Alien Invaders
  - Pinball
  - Retro Circuit
* **Space Wars** (`:feature:space-wars-kotlin`): Realtime space combat and strategy.

---

## 🛠️ Development & Build Tasks

Use these scripts and Gradle commands to run or compile target versions:

### Web (Wasm-JS)
* **Start Development Server**: 
  ```bash
  ./devserver
  ```
  *(Runs `:composeApp:wasmJsBrowserDevelopmentRun`)*
* **Build Production Bundle**: 
  ```bash
  ./compweb
  ```
  *(Runs `:composeApp:wasmJsBrowserDistribution`)*

### Desktop (Windows/Mac)
* **Run Desktop Application**:
  ```bash
  ./gradlew :composeApp:run
  ```
* **Package Native Distributions**:
  ```bash (for MacOs)
  ./globalsdwgradlew packageDmg
  ```
  ```windows
  gradlew.bat packageMsi
  ```

### Android
* **Assemble Debug APK**:
  ```bash
  ./gradlew :composeApp:assembleDebug
  ```

---

## 📐 Architecture & Libraries
This repository uses the latest KMP ecosystem standards:
* **UI Framework**: Compose Multiplatform with **Material 3 Adaptive Suite**.
* **State Management**: **Circum (MVI)** for state flow where designated.
* **Logging**: `club.gepetto.GcLog` (avoid standard println/Log/Timber).
* **Localization**: Partially localized across German (`de`), Spanish (`es`), French (`fr`), Italian (`it`), Portuguese (`pt`), and English (`en`).
