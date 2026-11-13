# iOS Port Plan: FunHouse Game Collection (Living Document)

> **Document Status**: Living Roadmap & Technical Specification  
> **Target Application**: FunHouse Game Collection (`/Users/luizvaldetaro/valdetaro/FunHouse`)  
> **Workspace**: `/Users/luizvaldetaro/valdetaro`  
> **Document Location**: `.agents/IOS_PORT_PLAN.md`  
> **Last Updated**: 2026-11-13 (Rev 6 — Audit vs. codebase: fixed library paths/build order, removed `iosX64`, completed iOS shims (Phase 1 would not compile Phase 2 as written), fixed resource install path, removed invalid framework `export`, replaced non-functional Xcode generator with XcodeGen, added Privacy Manifest, updated App Store age ratings)  
> **Current Phase**: Phase 1 (Core Models & Shims in :shared:common)

---

## Table of Contents
1. [Executive Summary & Current Architectural State](#1-executive-summary--current-architectural-state)
2. [Prerequisites & Development Tools Setup (Zero iOS Experience Guide)](#2-prerequisites--development-tools-setup-zero-ios-experience-guide)
3. [Testing Strategy Without a Physical iOS Device](#3-testing-strategy-without-a-physical-ios-device)
4. [Apple Developer Account & App Store Approval Process](#4-apple-developer-account--app-store-approval-process)
5. [Architectural Dependency Graph & Strategy](#5-architectural-dependency-graph--strategy)
6. [Detailed Technical Specification by Module](#6-detailed-technical-specification-by-module)
   - 6.1 [Step 0: Shared Libraries (`gepetto-utils`) KMP iOS Support](#61-step-0-shared-libraries-gepetto-utils-kmp-ios-support)
   - 6.2 [Step 1: FunHouse `:shared:common` Module & Runtime Shims](#62-step-1-funhouse-sharedcommon-module--runtime-shims)
   - 6.3 [Step 2: FunHouse 20 Feature Game Modules](#63-step-2-funhouse-20-feature-game-modules)
   - 6.4 [Step 3: FunHouse Engine Networking & Concurrency (`feature:funhouse-engine-kotlin`)](#64-step-3-funhouse-engine-networking--concurrency-featurefunhouse-engine-kotlin)
   - 6.5 [Step 4: `:composeApp` Module & `iosApp` Xcode Project Wrapper](#65-step-4-composeapp-module--iosapp-xcode-project-wrapper)
   - 6.6 [Step 5: Resource & File Installation Pipeline](#66-step-5-resource--file-installation-pipeline)
7. [Phase-by-Phase Execution Plan for Agents](#7-phase-by-phase-execution-plan-for-agents)
8. [Living Document Changelog](#8-living-document-changelog)
9. [Bug, Blocker & Issue Tracker](#9-bug-blocker--issue-tracker)

---

## 1. Executive Summary & Current Architectural State

The **FunHouse Game Collection** is a Kotlin Multiplatform (KMP) application built with **Compose Multiplatform**. It currently targets:
- **Android** (Phone, Tablet, TV)
- **Desktop** (macOS Apple Silicon/Intel, Windows)
- **Web** (WasmJS)

The project includes **20 games** spanning text-based adventures, interactive arcade/board/chance games, and chatbots:
- **Text Adventures / Engines**:
  - *FunHouse Engine* (`:feature:funhouse-engine-kotlin`): Powers **Island**, **FunHouse**, and **Space Station Aegis** (includes multiplayer WebSocket & discovery logic).
  - *Wander Engine* (`:feature:wander-engine-kotlin`): Powers **Wander Castle**, **Wander Aldebaran**, **Wander Library**, and **Wander Logic Ops**.
  - Classic Ports: **Colossal Cave Adventure**, **Chimaera**, **Dinkum**, **Space Wars**, **Mistery Mansion**, **Castle**, **Hangman**, **Secret Forest**, **Eliza**, **Wizard's Castle**.
- **Interactive Compose Games**:
  - **Blackjack**, **Slot Machine**, **Roulette**, **Craps**, **Poker**, **Chess**, **Classic Arcades** (PaddleBall, Alien Invaders, Pinball, Retro Circuit), and **Tetric** (secret game).

### Current Project Modularization
The Gradle build consists of 22 modules:
1. `:composeApp`: Main application entry point, adaptive windowing, navigation, and state dispatch.
2. `:shared:common`: Domain models (`Game`, `AppData`), sound player interfaces, file utilities, runtime platform shims, and common UI helpers.
3. 20 feature modules (`:feature:*`): Each housing self-contained game logic and UI.

The app depends on four shared KMP libraries. Each one is a **standalone Gradle project** under `~/valdetaro/GepettoLabs/` (the old multi-module `~/valdetaro/gepetto-utils` directory no longer exists):
- `GepettoLabs/gepetto-utils` → `club.gepetto:gepetto-utils` (FunHouse uses `2.1.2`)
- `GepettoLabs/circum` → `club.gepetto:circum` (`2.1.1`)
- `GepettoLabs/gclog` → `club.gepetto:gclog`
- `GepettoLabs/ads-lib` → `club.gepetto:gcadslib` (`0.4.1`)

All four already publish `iosArm64` / `iosSimulatorArm64` artifacts **to Maven Central** (verified 2026-11-13: gepetto-utils 2.1.3, circum 2.1.2, gclog 0.1.2, gcadslib 0.4.2), so other development machines do not need `mavenLocal()` for iOS.

### Current Toolchain & Dependencies (Updated 2026-11-13)
- **Java/JDK**: OpenJDK 21 (`JavaVersion.VERSION_21`, `JVM_21`)
- **Gradle Wrapper**: 9.7.1
- **Kotlin**: 2.4.21
- **App version**: `versionName = 2.1.64`, `versionCode = 164` (from `gradle/libs.versions.toml` — never hardcode these in iOS code)
- **Android Gradle Plugin (AGP)**: 9.4.0
- **JetBrains Compose Multiplatform**: 1.12.0
- **Navigation 3**: `androidx.navigation3:1.1.7`, `androidx.compose.material3.adaptive:1.3.0`

### Current Porting Obstacle
The shared libraries support iOS (Phase 0 done). `FunHouse` does not yet declare Apple iOS targets (`iosArm64`, `iosSimulatorArm64`; `iosX64` is NOT supported — see BUG-011). To run on iOS:
1. ~~Shared libraries must build for iOS targets and publish~~ — done (Maven Central + `mavenLocal()`).
2. FunHouse Gradle configurations must add iOS targets.
3. Native Apple framework bindings (`AVSpeechSynthesizer`, `NSFileManager`, `AVAudioPlayer`, `NSCalendar`) and Java/Android runtime shims (`java.io.File`, `BufferedReader`, `Thread.sleep`, `Random`) must be provided for `shared:common` and `funhouse-engine-kotlin`.
4. An `iosApp` Xcode project shell must be generated to wrap the Compose Multiplatform UI (`ComposeUIViewController`) into a native iOS application.

---

## 2. Prerequisites & Development Tools Setup (Zero iOS Experience Guide)

Because you have 0 experience developing for iOS and do not own an iOS device, here is what you need to know about your development environment on macOS:

### 2.1 Hardware & macOS Environment
- **Machine**: You are running macOS on an Apple Silicon / Intel Mac.
- **JDK**: Java 21 is required for all Gradle builds and builds run with `./gradlew`.
- **Xcode**: Apple's official IDE and toolchain for iOS.
  - Verification (2026-11-13) confirms Xcode is installed at `/Applications/Xcode.app` (Xcode 27.2, build 27B52) and active developer directory points to `/Applications/Xcode.app/Contents/Developer`.
  - iOS Simulator Runtime: **iOS 26.5** is installed; available devices include iPhone 17, 17 Pro, 17 Pro Max, 17e.
  - Development happens on more than one Mac: run the Section 2.2 commands on each machine before building.
- **XcodeGen** (used to generate `iosApp.xcodeproj`, see 6.5.3): `brew install xcodegen`.

### 2.2 First-Time Setup Commands
Run these commands once in your terminal to ensure permissions and licenses are accepted:
```bash
# 1. Ensure the active developer directory is set to Xcode
sudo xcode-select -s /Applications/Xcode.app/Contents/Developer

# 2. Accept the Apple Developer License agreement (required for command-line builds)
sudo xcodebuild -license accept

# 3. Verify that xcodebuild is functional
xcodebuild -version
```

### 2.3 Why CocoaPods is NOT Needed
In older Kotlin Multiplatform tutorials, CocoaPods was frequently recommended. **You do not need CocoaPods for FunHouse.**
Compose Multiplatform supports direct Xcode framework embedding via the Gradle task `:composeApp:embedAndSignAppleFrameworkForXcode`. This produces a lightweight, native Xcode project with zero third-party package manager dependencies.

---

## 3. Testing Strategy Without a Physical iOS Device

You **do not need to purchase or borrow an iPhone or iPad** to build, test, and polish FunHouse. Apple provides high-performance, cycle-accurate **iOS Simulators** inside macOS.

### 3.1 What is the Xcode iOS Simulator?
The iOS Simulator runs real iOS system binaries compiled for macOS architecture (ARM64 on Apple Silicon). It accurately mimics:
- Screen sizes, aspect ratios, notches, Dynamic Islands, and safe-area insets.
- Touch input via mouse clicks and trackpad gestures (pan, pinch, swipe).
- Software virtual keyboard and physical keyboard pass-through.
- Dark mode vs. Light mode system appearance.
- Audio playback and Text-to-Speech (plays directly through Mac speakers/headphones).
- Localhost and local Wi-Fi networking.

### 3.2 Listing and Booting Simulators
You can control the simulator completely from the terminal:
```bash
# List all installed simulators
xcrun simctl list devices available

# Boot an iPhone 17 (or iPhone 16) simulator
xcrun simctl boot "iPhone 17"

# Open the Simulator visual window on your Mac desktop
open -a Simulator
```

### 3.3 Building and Running FunHouse on the Simulator
Once the iOS target and Xcode project are set up:
1. **Build iOS Simulator Framework via Gradle**:
   ```bash
   cd /Users/luizvaldetaro/valdetaro/FunHouse
   ./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
   ```
   > [!NOTE]
   > For physical device testing, use `./gradlew :composeApp:linkDebugFrameworkIosArm64`.
   > The output framework is located at: `composeApp/build/bin/iosSimulatorArm64/debugFramework/ComposeApp.framework`.

2. **Build and Run via Terminal (`xcodebuild` + `simctl`)**:
   ```bash
   # Generate the Xcode project (see 6.5.3), then build for the simulator
   (cd iosApp && xcodegen)
   xcodebuild -project iosApp/iosApp.xcodeproj \
              -scheme iosApp \
              -destination 'platform=iOS Simulator,name=iPhone 17' \
              -configuration Debug \
              -derivedDataPath iosApp/build \
              build

   # Install the built .app onto the booted simulator
   # (-derivedDataPath fixes the output location; without it the .app goes to ~/Library/Developer/Xcode/DerivedData)
   xcrun simctl install booted iosApp/build/Build/Products/Debug-iphonesimulator/iosApp.app

   # Launch the app
   xcrun simctl launch booted com.gepetto.gamescollection
   ```
3. **Via Xcode GUI (Optional alternative)**:
   - Double-click `/Users/luizvaldetaro/valdetaro/FunHouse/iosApp/iosApp.xcodeproj`.
   - At the top bar, select target: `iosApp` -> `iPhone 17 (Simulator)`.
   - Press **Cmd + R** (or click the **Play** button).

### 3.4 Key Simulator Shortcuts for Testing
- **Toggle Dark / Light Mode**: `Cmd + Shift + A`
- **Rotate Device (Landscape / Portrait)**: `Cmd + Left Arrow` or `Cmd + Right Arrow`
- **Toggle Virtual Keyboard**: `Cmd + K`
- **Home Screen**: `Cmd + Shift + H`
- **Take App Store Screenshot**: `Cmd + S` (Saves directly to Mac Desktop with pixel-perfect resolution).

---

## 4. Apple Developer Account & App Store Approval Process

To publish on the Apple App Store, Apple requires registration with the Apple Developer Program and compliance with App Store Review Guidelines.

### 4.1 Step 1: Enrolling in the Apple Developer Program
1. **Apple ID**: Standard Apple ID with Two-Factor Authentication (2FA) enabled.
2. **Enrollment**:
   - Option A (Easiest): Open the **Apple Developer app** on your Mac (install from Mac App Store), sign in, and tap **Enroll**.
   - Option B: Visit [developer.apple.com/programs/enroll](https://developer.apple.com/programs/enroll/).
3. **Account Types**:
   - **Individual ($99 USD / year)**: Recommended for starting immediately. Approval within 24–48 hours. Personal legal name as developer name.
   - **Organization ($99 USD / year)**: Displays studio name (e.g. "Gepetto"). Requires a free D-U-N-S Number (takes 1–2 weeks).

### 4.2 Step 2: Certificates, Identifiers, and Signing
Once enrolled:
1. **Bundle Identifier**: Register explicit App ID: `com.gepetto.gamescollection`.
2. **Xcode Automatic Signing**:
   - In Xcode -> **Settings** -> **Accounts**, sign in with your Apple ID.
   - In `iosApp` target settings -> **Signing & Capabilities**, check **"Automatically manage signing"** and select Team.

### 4.3 Step 3: App Store Approval Guidelines & FunHouse Specific Gotchas

#### A. Game Collections (Guidelines 2.1 / 4.2)
- FunHouse contains 20 classic games compiled into one binary. This is an ordinary app. Guideline 4.7 (mini apps, HTML5 mini games, streaming games, emulators) does **not** apply, because no game code is downloaded or run outside the binary.
- Every game in the collection must be fully functional and stable (Guideline 2.1 — App Completeness). A game that crashes or shows an empty screen on iOS is a rejection risk; hide it on iOS until it works.

#### B. The "Simulated Gambling" Age Rating Gotcha (CRITICAL)
- FunHouse includes **Blackjack**, **Craps**, **Roulette**, **Slot Machine**, and **Poker**.
- In the App Store Connect Age Rating questionnaire, you **MUST** declare:
  - **Simulated Gambling**: Answer **"Frequent / Intense"** (or "Infrequent/Mild").
  - Apple calculates the rating from the answers. Apple's current rating scale (since 2025) is **4+, 9+, 13+, 16+, 18+** — the old 12+/17+ values no longer exist. Expect a 13+ or higher rating; read the result in App Store Connect after you answer.
  - **Crucial Note**: The app description must clearly state:
    > *"All casino games (Blackjack, Craps, Roulette, Slot Machine, Poker) are for entertainment purposes only. The app uses virtual chips and credits. No real money gambling or real prizes are offered or won."*
  - Failure to declare simulated gambling will result in immediate rejection under Guideline 2.3 (Accurate Metadata).

#### C. Intellectual Property & Copyrights (Guideline 5.2)
- **Tetric**: Keep the game hidden by default (`AppData.secretGamesEnabled = false` in release builds) to prevent trademark disputes with The Tetris Company.
- **Classic Games**: Colossal Cave Adventure, Eliza, Castle, Wander, Dinkum, Chimaera, etc., are covered under open-source licenses (BSD, GNU, public domain). The existing in-app "About" and license files (`bsdlicense.txt`, `gnulicense.txt`, `funhouselicense.txt`, `islandlicense.txt`) provide legal attribution.

#### D. Privacy Policy & App Nutrition Labels (Guideline 5.1)
- Host `privacy_en.md` (and localized variants) on a public website.
- In App Store Connect App Privacy: Declare "Data Not Collected" (unless you wire AdMob/Analytics in Phase 5).

#### E. Mandatory `Info.plist` Keys
```xml
<!-- Avoid complex US export compliance questionnaires -->
<key>ITSAppUsesNonExemptEncryption</key>
<false/>

<!-- Local network permissions (only if multiplayer engine is enabled) -->
<key>NSLocalNetworkUsageDescription</key>
<string>FunHouse uses the local network to discover and connect with other players for multiplayer adventure games.</string>
<key>NSBonjourServices</key>
<array>
    <!-- Must match the Android NSD type in NsdHelper.kt ("_funhouse-game._tcp.") -->
    <string>_funhouse-game._tcp</string>
</array>
```
> [!NOTE]
> The local network keys are only needed when iOS multiplayer is implemented. Phase 2 uses stubs for sockets and discovery, so leave these keys out of the first builds.

#### E.2 Mandatory Privacy Manifest (`PrivacyInfo.xcprivacy`)
Since May 2024 Apple rejects uploads that use "required reason" APIs without a privacy manifest. FunHouse (through `gcadslib` and `gepetto-utils`) uses:
- `NSUserDefaults` (`checkFirstRun` in ads-lib) → category `NSPrivacyAccessedAPICategoryUserDefaults`, reason `CA92.1`.
- File timestamps (`NSFileModificationDate` in `PlatformFile.lastModified`) → category `NSPrivacyAccessedAPICategoryFileTimestamp`, reason `C617.1`.

Add `iosApp/iosApp/PrivacyInfo.xcprivacy` to the app target:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>NSPrivacyTracking</key>
    <false/>
    <key>NSPrivacyCollectedDataTypes</key>
    <array/>
    <key>NSPrivacyAccessedAPITypes</key>
    <array>
        <dict>
            <key>NSPrivacyAccessedAPIType</key>
            <string>NSPrivacyAccessedAPICategoryUserDefaults</string>
            <key>NSPrivacyAccessedAPITypeReasons</key>
            <array><string>CA92.1</string></array>
        </dict>
        <dict>
            <key>NSPrivacyAccessedAPIType</key>
            <string>NSPrivacyAccessedAPICategoryFileTimestamp</string>
            <key>NSPrivacyAccessedAPITypeReasons</key>
            <array><string>C617.1</string></array>
        </dict>
    </array>
</dict>
</plist>
```
Update this file in Phase 5 if AdMob is added (the Google SDK ships its own manifest, but `NSPrivacyTracking` and collected data types for the app must then be updated).

#### F. App Store Assets
- **App Icon**: 1024x1024 PNG (no transparency, square corners — iOS applies corner rounding automatically).
- **Screenshots**:
  - 6.9-inch iPhone (iPhone 17 Pro Max simulator) — 1320 x 2868 px (the 6.9" set is the required iPhone set; smaller sizes are scaled from it).
  - 13-inch iPad — 2064 x 2752 px (or 2048 x 2732 px), required only if iPad is supported.
  - Check the current required sizes in App Store Connect before capture.
  - Can be captured directly from Xcode Simulator (`Cmd + S`).

#### G. Google AdMob & App Store Privacy / ATT Compliance (Mandatory for iOS Monetization)
1. **App Tracking Transparency (ATT) — Guideline 5.1.2**:
   - `Info.plist` key: `NSUserTrackingUsageDescription`.
   - Native ATT authorization dialog must be presented before requesting personalized ads.
2. **`GADApplicationIdentifier` in `Info.plist` (CRITICAL CRASH RISK)**:
   - Google AdMob on iOS **will crash on launch** with `NSInvalidArgumentException` if `GADApplicationIdentifier` is missing.
3. **SKAdNetwork Identifiers**: Must be added to `Info.plist`.
4. **Consent (EEA/UK)**: Google requires a certified consent platform for personalized ads in the EEA/UK. Use Google's User Messaging Platform (UMP) SDK and show its form before the ATT prompt.

---

## 5. Architectural Dependency Graph & Strategy

```
+---------------------------------------------------------------------------------------+
|                                      ~/valdetaro                                      |
+---------------------------------------------------------------------------------------+
                                           |
         +---------------------------------+---------------------------------+
         |                                                                   |
         v                                                                   v
 [GepettoLabs/* (4 projects)] [DONE]                                 [FunHouse Project]
  Step 0.1: circum         (no deps)                                 Step 1: shared:common
  Step 0.2: gclog          (no deps)                                         (iOS actuals + runtime shims)
  Step 0.3: gepetto-utils  (depends on circum)                               |
  Step 0.4: ads-lib        (depends on gepetto-utils & gclog)                v
         |                                                           Step 2: 20 Feature Game Modules
         +------------------ publishToMavenLocal -------------------> Step 3: funhouse-engine-kotlin
                                                                             |
                                                                             v
                                                                     Step 4: composeApp (framework)
                                                                             |
                                                                             v
                                                                     Step 5: iosApp (Xcode shell)
```

### Execution Strategy
1. **Phase 0 (Foundation) [DONE]**: iOS targets added to the 4 libraries under `GepettoLabs/` in dependency order (`circum` -> `gclog` -> `gepetto-utils` -> `ads-lib`, as in `GepettoLabs/publishlocal`) and published (mavenLocal and Maven Central).
2. **Phase 1 (Core Models & Shims)**: Add iOS targets to `:shared:common` and implement native iOS actuals (TTS, files, time, sound) PLUS Java/Android runtime shims (`java.io.File`, `BufferedReader`, `Thread.sleep`, `Random`).
3. **Phase 2 (Game Features)**: Add iOS targets to all 20 `:feature:*` modules and provide iOS actuals for `funhouse-engine-kotlin`.
4. **Phase 3 (App Shell)**: Add iOS static framework target to `:composeApp` and generate the `iosApp` Xcode project.
5. **Phase 4 (Asset Pipeline & Simulator Testing)**: Verify asset file installation and test on iOS simulator.
6. **Phase 5 (Monetization)**: Wire AdMob via Swift Package Manager and ATT dialog (when ready).
7. **Phase 6 (Distribution)**: Prepare App Store metadata, screenshots, and archive.

---

## 6. Detailed Technical Specification by Module

#### 6.1 Step 0: Shared Libraries (`GepettoLabs`) KMP iOS Support [COMPLETED & PUBLISHED]
Path: `/Users/luizvaldetaro/valdetaro/GepettoLabs/{circum,gclog,gepetto-utils,ads-lib}` — four **standalone** Gradle projects (each has its own `gradlew` and `gradle/libs.versions.toml`). The text below records the work as it was done in Rev 5, when the libraries were still one multi-module project at `~/valdetaro/gepetto-utils`; that directory no longer exists. Module paths such as `circum/build.gradle.kts` now mean `GepettoLabs/circum/circum/build.gradle.kts`.

> [!NOTE]
> **Phase 0 Status: COMPLETE & PUBLISHED TO MAVENLOCAL (2026-09-18)**  
> All 4 shared libraries in `gepetto-utils` have been updated with modern Apple iOS targets (`iosArm64`, `iosSimulatorArm64`), all 15 native iOS actual files implemented, all compilation targets verified cleanly, and published to `~/.m2/repository/club/gepetto/`.
> 
> **Published Version Coordinates**:
> - `club.gepetto:circum:2.1.1`
> - `club.gepetto:gepetto-utils:2.1.1`
> - `club.gepetto:gclog:0.1.1`
> - `club.gepetto:gcadslib:0.4.1`

#### Target Architecture Decision: Modern Apple Silicon
Compose Multiplatform 1.12.0 and AndroidX Lifecycle 2.11.0 no longer publish `iosX64` binaries. Attempting to declare `iosX64()` results in Gradle dependency resolution failure. Modern iOS development is 100% ARM64:
- `iosSimulatorArm64()`: Apple Silicon Macs running iOS Simulator (iPhone 17, iOS 26.5)
- `iosArm64()`: Physical 64-bit iPhone and iPad hardware

#### Build Dependency Sequence
Correct dependency order (matches `GepettoLabs/publishlocal`):
1. `circum` (no internal deps)
2. `gclog` (no internal deps — it does **not** depend on gepetto-utils)
3. `gepetto-utils` (depends on `circum`)
4. `ads-lib` (depends on `gepetto-utils` and `gclog`)

---

#### 6.1.1 `:circum` Module
- **`circum/build.gradle.kts`**: Added `iosArm64()` and `iosSimulatorArm64()`.
- **`circum/src/iosMain/kotlin/club/gepetto/circum/CircumIntentProcessorFunctions.ios.kt`**:
  ```kotlin
  package club.gepetto.circum

  import platform.darwin.dispatch_async
  import platform.darwin.dispatch_get_main_queue

  internal actual fun runOnMainThread(action: () -> Unit) {
      dispatch_async(dispatch_get_main_queue()) {
          action()
      }
  }
  ```

---

#### 6.1.2 `:gepetto-utils` Module
- **`gepetto-utils/build.gradle.kts`**:
  - Configured `applyDefaultHierarchyTemplate()` inside `kotlin { ... }`.
  - Added `iosArm64()` and `iosSimulatorArm64()`.
  - Added `named("iosMain") { dependencies { implementation(libs.ktor.client.darwin) } }`.
- **`gradle/libs.versions.toml`**:
  - Added `ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktorClientCore" }`.
  - Synced library version properties: `circumVersion = "2.1.1"`, `gepettoUtilsVersion = "2.1.1"`, `gcLogVersion = "0.1.1"`, `gepettoAdsLib = "0.4.1"`.
- **Created 11 iOS Actual Files in `gepetto-utils/src/iosMain/kotlin/`**:
  1. `club/gepetto/composeutils/PlatformFile.ios.kt`: Complete iOS file system actual using `NSFileManager` and `NSData`. Includes `@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)`, empty byte array guard before `pinned.addressOf(0)`, and path normalization in constructors and `parentFile`.
  2. `club/gepetto/composeutils/PlatformBitmap.ios.kt`: `actual class PlatformBitmap(val imageBitmap: ImageBitmap)` with `toImageBitmap()` extension.
  3. `club/gepetto/composeutils/PlatformHttpClient.ios.kt`: `createPlatformHttpClient` configured with `Darwin` engine.
  4. `club/gepetto/composeutils/GcCurrentTimeMillis.ios.kt`: `gcCurrentTimeMillis()` via `(NSDate().timeIntervalSince1970 * 1000.0).toLong()`.
  5. `club/gepetto/composeutils/Actuals.ios.kt`: `Context`, `QrCodeView`, `isAndroidPlatform = false`, `BackHandler`, drawable/asset stubs, and `textAsBitmap`.
  6. `club/gepetto/composeutils/GcQrCodeScanner.ios.kt`: Composable UI placeholder for iOS QR scanning.
  7. `club/gepetto/composeutils/image/ImageActuals.ios.kt`: `LegacyImageResource`, `GcFullImagePopup`, and Coil3 `gCnewImageLoader(PlatformContext.INSTANCE)`.
  8. `club/gepetto/composeutils/webpage/WebComposeUtils.ios.kt`: `GcHtmlView`, `GcHtmlText`, and `GcHtmlFile`.
  9. `club/gepetto/utils/Dispatcher.ios.kt`: `actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default`.
  10. `club/gepetto/utils/Utils.ios.kt`: `gCSpeak` using lazy `AVSpeechSynthesizer()` to prevent audio engine initialization before app runloop is active; `isRunningOnChromebook = false`.
  11. `club/gepetto/utils/GcAppInfo.ios.kt`: `actual object GcAppInfo` application metadata holder.

---

#### 6.1.3 `:gclog` Module
- **`gclog/build.gradle.kts`**: Added `iosArm64()` and `iosSimulatorArm64()`.
- **`gclog/src/iosMain/kotlin/club/gepetto/GcLog.kt`**:
  - Implemented under `package club.gepetto` (matching common expect package).
  - Implemented `formatString` with regex specifier replacement (`%[\\d\\.]*[a-zA-Z]`), `getStackTag() = "GcLogIos"`, and format-string-safe `NSLog("%s", fullMessage)`.

---

#### 6.1.4 `:ads-lib` Module
- **`ads-lib/build.gradle.kts`**: Added `iosArm64()` and `iosSimulatorArm64()`.
- **`ads-lib/src/iosMain/kotlin/club/gepetto/gcadslib/Actuals.ios.kt`**:
  - Implemented `Bundle`, `initMobileAds`, `initAnalytics`, `initAnalyticsAndAds`.
  - Implemented persistent first-launch detection in `checkFirstRun(context)` via `NSUserDefaults.standardUserDefaults`.
  - Implemented `actual object AnalyticsTracker` with all tracking methods.
- **`ads-lib/src/iosMain/kotlin/club/gepetto/gcadslib/ui/ActualsUi.ios.kt`**:
  - Implemented `NativeAd`, `AdInterstitial`, `InterstitialAd`, and composable stubs for all ad sizes (`QuarterScreen`, `EightScreen`, `HalfScreen`, `Banner`, `LargeBanner`, `FullBanner`, `Leaderboard`, `MediumRectangle`, `AdNative`, `AdBanner`, `AdBannerAdaptive`, `GcAd`).

---

#### 6.1.5 Publishing to `mavenLocal()` & Target Verification
- **Publish Command (current layout)**:
  ```bash
  cd /Users/luizvaldetaro/valdetaro/GepettoLabs
  ./publishlocal
  ```
  (`publishlocal` runs `./gradlew publishToMavenLocal` in `circum`, `gclog`, `gepetto-utils`, `ads-lib`, in that order.) In Rev 5 all 4 modules were published (`BUILD SUCCESSFUL in 46s`). Verified presence of both `iossimulatorarm64` and `iosarm64` klib, metadata, and POM files under `~/.m2/repository/club/gepetto/`.

- **Regression & iOS Verification**:
  ```bash
  # Existing targets (Android, Desktop, WasmJs)
  ./gradlew --no-daemon compileDebugKotlinAndroid compileKotlinDesktop compileKotlinWasmJs
  # Result: BUILD SUCCESSFUL in 19s

  # iOS targets across all 4 modules
  ./gradlew --no-daemon compileKotlinIosSimulatorArm64 compileKotlinIosArm64
  # Result: BUILD SUCCESSFUL in 6s
  ```

---

## 6.2 Step 1: FunHouse `:shared:common` Module & Runtime Shims
Path: `/Users/luizvaldetaro/valdetaro/FunHouse/shared/common`

### 6.2.1 `build.gradle.kts` Updates
Add iOS targets to `shared/common/build.gradle.kts`.
> [!IMPORTANT]
> Do NOT add `binaries.framework` here. Only `:composeApp` produces the final application framework.
> Add `compilerOptions { freeCompilerArgs.add("-Xallow-kotlin-package") }` so `package kotlin` shims compile.

```kotlin
kotlin {
    // Android, JVM desktop, WasmJs...
    // iosX64 is NOT supported by Compose 1.12 / Lifecycle 2.11 (BUG-011) — do not add it.
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.compilerOptions {
            freeCompilerArgs.add("-Xallow-kotlin-package")
        }
    }
}
```

### 6.2.2 Critical Runtime Shims in `shared/common/src/iosMain/kotlin/`
Game modules use JVM APIs (`java.io.*`, `java.util.Random`, `Thread`, `Math`, `System`, `removeIf`, `ticker`) in `commonMain`. The web build already solves this with shims in `shared/common/src/wasmJsMain/kotlin/`. The iOS shims follow the same packages and the same API surface. **Rule: every public declaration in the wasm shims that common code uses must also exist in the iOS shims.** Before Phase 2, diff the two folders.

Usages in common code that the shims must cover (found 2026-11-13):
| API | Used in |
|---|---|
| `java.io.File`, `BufferedReader`, `BufferedWriter`, `FileReader`, `IOException`, `Serializable` | many modules (14 `import java.io.File`) |
| `java.util.Random(seed)` | `ChimaeraKotlin.kt:543`, `WizardsCastleKotlin.kt:716` |
| `Thread.sleep` | castle, chimaera, colossal-cave, dinkum, wizards-castle, `FunHouseIntentProcessor.kt:400` |
| `InterruptedException("…")` (String constructor) | `CastleKotlin.kt:1084` and the other `myExit` functions |
| `System.err.println` | `AdventureKotlin.kt:345` |
| `Math.abs/min/max` (Int), `Math.toRadians` | chess `ChessAI.kt`, dinkum, `PinballView.kt:443` |
| `MutableCollection.removeIf` | `InvadersView.kt:154` |
| `kotlinx.coroutines.channels.ticker` (JVM-only API) | tetric `GameButton.kt:50` |
| `android.content.Context`, `android.media.*`, `SuppressLint`, `LocalContext` | various |

`kotlinx.coroutines.IO` does **not** need a shim on iOS: `Dispatchers.IO` is public on Kotlin/Native (import `kotlinx.coroutines.IO`, as `FunHouseIntentProcessor` already does). Do **not** copy `wasmJsMain/kotlin/kotlinx/coroutines/DispatchersIO.kt` to iOS — it would clash with the real declaration.

`kotlin.concurrent.thread` and `java.util.concurrent.LinkedBlockingQueue` exist in the wasm shims but are not used by common code today (games use coroutines and `GcInputQueue`). Do not port them unless a compile error asks for them.

Threading note: text games run their loop in `CoroutineScope(Dispatchers.Default)`, so a blocking `Thread.sleep` (via `usleep`) blocks a worker thread, not the UI thread. This matches desktop behavior.

#### A. `java/io/File.kt`:
```kotlin
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package java.io

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*

interface Serializable

interface File {
    val path: String
    val absolutePath: String
    val isAbsolute: Boolean
    val name: String
    fun exists(): Boolean
    fun mkdir(): Boolean
    fun mkdirs(): Boolean
    fun delete(): Boolean
    fun readText(): String
    fun writeText(text: String)
    fun readBytes(): ByteArray
    fun bufferedReader(): BufferedReader
    fun bufferedWriter(): BufferedWriter
    fun copyTo(target: File, overwrite: Boolean = false): File
    fun forEachLine(action: (String) -> Unit)
    fun printWriter(): PrintWriter
}

class FileImpl(override val path: String) : File {
    override val absolutePath: String get() = path
    override val isAbsolute: Boolean get() = path.startsWith("/")
    override val name: String get() = path.substringAfterLast('/')

    override fun exists(): Boolean = NSFileManager.defaultManager.fileExistsAtPath(path)
    override fun mkdir(): Boolean =
        NSFileManager.defaultManager.createDirectoryAtPath(path, withIntermediateDirectories = false, attributes = null, error = null)
    override fun mkdirs(): Boolean =
        NSFileManager.defaultManager.createDirectoryAtPath(path, withIntermediateDirectories = true, attributes = null, error = null)
    override fun delete(): Boolean = NSFileManager.defaultManager.removeItemAtPath(path, error = null)

    override fun readText(): String {
        val data = NSData.dataWithContentsOfFile(path) ?: return ""
        return NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString() ?: ""
    }

    override fun writeText(text: String) {
        val nsStr = NSString.create(string = text)
        nsStr.writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
    }

    // Binary-safe: do NOT go through readText() (it corrupts images and other binary files).
    override fun readBytes(): ByteArray {
        val data = NSData.dataWithContentsOfFile(path) ?: return ByteArray(0)
        val size = data.length.toInt()
        if (size == 0) return ByteArray(0)
        return ByteArray(size).apply {
            usePinned { pinned -> platform.posix.memcpy(pinned.addressOf(0), data.bytes, data.length) }
        }
    }
    override fun bufferedReader(): BufferedReader = BufferedReader(this)
    override fun bufferedWriter(): BufferedWriter = BufferedWriter(this)
    // Binary-safe copy through NSFileManager.
    override fun copyTo(target: File, overwrite: Boolean): File {
        val fm = NSFileManager.defaultManager
        if (fm.fileExistsAtPath(target.path)) {
            if (!overwrite) return target
            fm.removeItemAtPath(target.path, error = null)
        }
        fm.copyItemAtPath(path, toPath = target.path, error = null)
        return target
    }
    override fun forEachLine(action: (String) -> Unit) {
        val text = readText()
        if (text.isNotEmpty()) text.lines().forEach(action)
    }
    override fun printWriter(): PrintWriter = PrintWriter(this)
}

fun File(path: String): File = FileImpl(path)
fun File(parent: String?, child: String): File = FileImpl(if (parent != null) "$parent/$child" else child)
fun File(parent: File?, child: String): File = FileImpl(if (parent != null) "${parent.path}/$child" else child)

class FileReader(val file: File)
class FileWriter(val file: File)

class IOException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}

class BufferedReader(val source: Any) : AutoCloseable {
    private val content: String = when (source) {
        is FileReader -> source.file.readText()
        is File -> source.readText()
        else -> ""
    }
    private val lines = content.lines()
    private var index = 0
    private var charIndex = 0

    fun readLine(): String? = if (index < lines.size) lines[index++] else null
    fun read(): Int = if (charIndex < content.length) content[charIndex++].code else -1
    override fun close() {}
}

class BufferedWriter(val source: Any?) : AutoCloseable {
    private val file: File? = when (source) {
        is FileWriter -> source.file
        is File -> source
        else -> null
    }
    private val sb = StringBuilder()
    fun write(str: String) { sb.append(str) }
    fun newLine() { sb.append("\n") }
    override fun close() { file?.writeText(sb.toString()) }
    fun flush() { file?.writeText(sb.toString()) }
}

class PrintWriter(val source: Any?) : AutoCloseable {
    private val file: File? = when (source) {
        is File -> source
        else -> null
    }
    private val sb = StringBuilder()
    fun println(x: Any?) { sb.append(x.toString()).append("\n") }
    fun println(x: String?) { sb.append(x ?: "null").append("\n") }
    fun println(x: Int) { sb.append(x).append("\n") }
    fun println(x: Long) { sb.append(x).append("\n") }
    fun println(x: Double) { sb.append(x).append("\n") }
    fun println(x: Boolean) { sb.append(x).append("\n") }
    override fun close() { file?.writeText(sb.toString()) }
}
```

#### B. `java/util/Random.kt`:
```kotlin
package java.util

import kotlin.random.Random as KotlinRandom

// The seed MUST be honored: Chimaera and Wizard's Castle create seeded generators.
// (The sequence differs from java.util.Random's LCG, but the same seed gives the same sequence on iOS.)
class Random {
    private val rnd: KotlinRandom
    constructor() { rnd = KotlinRandom.Default }
    constructor(seed: Long) { rnd = KotlinRandom(seed) }
    fun nextInt(): Int = rnd.nextInt()
    fun nextInt(bound: Int): Int = rnd.nextInt(bound)
    fun nextDouble(): Double = rnd.nextDouble()
    fun nextFloat(): Float = rnd.nextFloat()
    fun nextLong(): Long = rnd.nextLong()
    fun nextBoolean(): Boolean = rnd.nextBoolean()
}

class Locale {
    companion object { fun getDefault(): Locale = Locale() }
    val language: String get() = "en"
}
```

#### C. `kotlin/SystemAndThread.kt`:
```kotlin
package kotlin

import java.io.PrintWriter
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.math.pow as kotlinPow
import kotlin.math.round as kotlinRound

class Thread {
    private var interrupted = false
    fun interrupt() { interrupted = true }
    val isInterrupted: Boolean get() = interrupted

    companion object {
        fun sleep(millis: Long) {
            if (millis <= 0) return
            // Sleep in chunks below 1 s: usleep() may reject values >= 1,000,000 µs.
            var remaining = millis
            while (remaining > 0) {
                val chunk = minOf(remaining, 900L)
                platform.posix.usleep((chunk * 1000L).toUInt())
                remaining -= chunk
            }
        }
        fun currentThread(): Thread = Thread()
    }
}

// Games call both forms, e.g. throw InterruptedException("Game Restarted") in CastleKotlin.myExit().
class InterruptedException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}

object System {
    fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
    val err = PrintWriter(null)   // used by AdventureKotlin.kt (System.err.println)
    fun exit(status: Int) {}
}

// Same surface as wasmJsMain/kotlin/kotlin/SystemAndThread.kt
object Math {
    fun abs(x: Int): Int = kotlin.math.abs(x)
    fun abs(x: Long): Long = kotlin.math.abs(x)
    fun abs(x: Float): Float = kotlin.math.abs(x)
    fun abs(x: Double): Double = kotlin.math.abs(x)
    fun min(a: Int, b: Int): Int = kotlin.math.min(a, b)
    fun min(a: Long, b: Long): Long = kotlin.math.min(a, b)
    fun min(a: Float, b: Float): Float = kotlin.math.min(a, b)
    fun min(a: Double, b: Double): Double = kotlin.math.min(a, b)
    fun max(a: Int, b: Int): Int = kotlin.math.max(a, b)
    fun max(a: Long, b: Long): Long = kotlin.math.max(a, b)
    fun max(a: Float, b: Float): Float = kotlin.math.max(a, b)
    fun max(a: Double, b: Double): Double = kotlin.math.max(a, b)
    fun pow(a: Double, b: Double): Double = a.kotlinPow(b)
    fun sqrt(x: Double): Double = kotlin.math.sqrt(x)
    fun random(): Double = kotlin.random.Random.nextDouble()
    fun round(x: Double): Long = kotlinRound(x).toLong()
    fun floor(x: Double): Double = kotlin.math.floor(x)
    fun ceil(x: Double): Double = kotlin.math.ceil(x)
    fun toRadians(angdeg: Double): Double = angdeg / 180.0 * kotlin.math.PI   // PinballView.kt
}
```

#### C.2 `kotlin/collections/CollectionsCompat.kt` (copy of the wasm shim — used by `InvadersView.kt`):
```kotlin
package kotlin.collections

inline fun <T> MutableCollection<T>.removeIf(filter: (T) -> Boolean): Boolean {
    val iterator = iterator()
    var removed = false
    while (iterator.hasNext()) {
        if (filter(iterator.next())) { iterator.remove(); removed = true }
    }
    return removed
}
```

#### C.3 `kotlinx/coroutines/channels/TickerShim.kt` (copy of the wasm shim — `ticker` is JVM-only; used by tetric `GameButton.kt`):
```kotlin
package kotlinx.coroutines.channels

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ObsoleteCoroutinesApi
fun ticker(
    delayMillis: Long,
    initialDelayMillis: Long = 0,
    context: kotlin.coroutines.CoroutineContext = kotlin.coroutines.EmptyCoroutineContext,
    mode: Any? = null
): ReceiveChannel<Unit> {
    val channel = Channel<Unit>(Channel.CONFLATED)
    @OptIn(DelicateCoroutinesApi::class)
    GlobalScope.launch(context) {
        delay(initialDelayMillis)
        while (true) { channel.send(Unit); delay(delayMillis) }
    }
    return channel
}
```
> [!NOTE]
> This shim (like the wasm one) never stops the coroutine after `channel.cancel()`; `send` then throws and ends it. That matches web behavior, so keep it for parity.

#### D. `android/` and `androidx/` shims:
- `android/content/Context.kt`:
  ```kotlin
  package android.content
  class Context { val resources: Resources = Resources() }
  class Resources { fun getIdentifier(name: String, defType: String, defPackage: String): Int = 0 }
  ```
- `android/media/MediaMocks.kt`:
  ```kotlin
  package android.media
  object AudioManager { const val STREAM_MUSIC = 3 }
  class SoundPool {
      fun load(context: Any?, resId: Int, priority: Int): Int = 0
      fun play(soundID: Int, leftVolume: Float, rightVolume: Float, priority: Int, loop: Int, rate: Float): Int = 0
      fun release() {}
      class Builder { fun setMaxStreams(maxStreams: Int): Builder = this; fun build(): SoundPool = SoundPool() }
  }
  ```
- `android/annotation/SuppressLint.kt`:
  ```kotlin
  package android.annotation
  @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
  annotation class SuppressLint(vararg val value: String)
  ```
- `androidx/compose/ui/platform/LocalContext.kt`:
  ```kotlin
  package androidx.compose.ui.platform
  import androidx.compose.runtime.Composable
  object LocalContext { val current: Any? @Composable get() = null }
  ```

### 6.2.3 Platform Helpers & Game Sounds Actuals
1. `com/funhouse/shared/common/utils/PlatformHelpersIos.kt`:
```kotlin
package com.funhouse.shared.common.utils

import com.funhouse.shared.common.AppData
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.Foundation.*
import org.jetbrains.skia.Image
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import com.funhouse.shared.common.generated.resources.Res
import club.gepetto.GcLog

// Lazy: do not start the audio engine before the app run loop is active (same as gepetto-utils Utils.ios.kt).
private val speechSynthesizer by lazy { AVSpeechSynthesizer() }

actual fun speakText(text: String) {
    if (text.isBlank()) return
    speechSynthesizer.speakUtterance(AVSpeechUtterance(string = text))
}

actual fun readAssetFile(fileName: String): String? {
    val folder = AppData.gameFolderFile as? java.io.File
    val path = folder?.path ?: return null
    val fullPath = "$path/$fileName"
    val data = NSData.dataWithContentsOfFile(fullPath) ?: return null
    return NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString()
}

actual fun saveTextToFile(fileName: String, text: String) {
    val folder = AppData.packageFolder
    val fullPath = "$folder/$fileName"
    val nsString = NSString.create(string = text)
    nsString.writeToFile(fullPath, atomically = true, encoding = NSUTF8StringEncoding, error = null)
}

actual fun readTextFromFile(fileName: String): String? {
    val folder = AppData.packageFolder
    val fullPath = "$folder/$fileName"
    val data = NSData.dataWithContentsOfFile(fullPath) ?: return null
    return NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString()
}

// Do NOT guess bundle paths. The desktop build stores files under
// "composeResources/com.funhouse.shared.common.generated.resources/files/"; the old candidate list
// left out the "composeResources/" segment, so no path matched and the function returned silently.
// Use the Compose Resources API instead (the same API the wasm entry point uses).
@OptIn(ExperimentalResourceApi::class, ExperimentalForeignApi::class)
actual fun installFile(name: String, overwrite: Boolean) {
    val fileManager = NSFileManager.defaultManager
    val destFolder = AppData.gameFolderFile as? java.io.File ?: return
    if (!fileManager.fileExistsAtPath(destFolder.path)) destFolder.mkdirs()
    val destPath = "${destFolder.path}/$name"
    if (fileManager.fileExistsAtPath(destPath) && !overwrite) return

    val bytes: ByteArray = runBlocking {
        try {
            Res.readBytes("files/$name")
        } catch (e: Exception) {
            try { Res.readBytes("drawable/$name") } catch (e2: Exception) { null }
        }
    } ?: run {
        GcLog.e("installFile: resource not found: $name")
        return
    }
    val data = bytes.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
    }
    if (!data.writeToFile(destPath, atomically = true)) {
        GcLog.e("installFile: write failed: $destPath")
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun loadImageBitmapFromFile(fileName: String): ImageBitmap? {
    val folder = AppData.gameFolderFile as? java.io.File ?: return null
    val fullPath = "${folder.path}/$fileName"
    val data = NSData.dataWithContentsOfFile(fullPath) ?: return null
    val bytes = ByteArray(data.length.toInt()).apply {
        usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), data.bytes, data.length)
        }
    }
    return try {
        Image.makeFromEncoded(bytes).toComposeImageBitmap()
    } catch (e: Exception) {
        null
    }
}

actual fun stopGameThread() {}

private val terminalLines = mutableListOf<String>()
actual fun appendTerminalText(text: String) {
    terminalLines.addAll(text.lines().map { it.trim() }.filter { it.isNotEmpty() })
    while (terminalLines.size > 20) terminalLines.removeAt(0)
}
actual fun getLatestTerminalText(): String = terminalLines.joinToString("\n")
actual fun clearTerminalText() { terminalLines.clear() }

actual val isWebTarget: Boolean = false
actual val isLocalWebSocketSupported: Boolean = true
```

2. `com/funhouse/shared/common/jni/BaseKotlinGameSoundsIos.kt`:
```kotlin
package com.funhouse.shared.common.jni

actual fun playBicycle() {}
actual fun haltBicycle() {}
actual fun playCoin() {}
actual fun playBell() {}
actual fun playJackpot() {}
actual fun playJackpotBigger() {}
actual fun playJackpotMusic() {}
actual fun playTennisBall() {}
actual fun playFlip() {}
actual fun playChip() {}
actual fun playBump() {}
actual fun playBoing() {}
actual fun playDice() {}
```

3. `com/funhouse/shared/common/utils/TimeHelpersIos.kt`, `BackHandlerIos.kt`, `CacheHelper.ios.kt`:
- `getCurrentTime()`: `(components.hour.toInt() to components.minute.toInt())` via `NSCalendar.currentCalendar`.
- `CommonBackHandler`: no-op Composable.
- `getCacheMap()`: returns `mutableMapOf()`.

---

## 6.3 Step 2: FunHouse 20 Feature Game Modules
Path: `/Users/luizvaldetaro/valdetaro/FunHouse/feature/*`

Add iOS targets to all 20 feature modules in `feature/*/build.gradle.kts`:
```kotlin
kotlin {
    androidTarget { ... }
    jvm("desktop") { ... }
    wasmJs { ... }

    // Add iOS targets (no iosX64 — BUG-011):
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies { ... }
    }
}
```

19 modules have only `commonMain`; `funhouse-engine-kotlin` also has platform source sets (6.4). The 19 modules depend on `:shared:common` and use its shims, so they compile for iOS without code changes **only if** the shims in 6.2.2 are complete (including `InterruptedException(String)`, `System.err`, all `Math` overloads, `removeIf`, and `ticker`). If a feature module fails to compile, fix the shim in `shared/common/src/iosMain`, not the game code (classic games must not change).

---

## 6.4 Step 3: FunHouse Engine Networking & Concurrency (`feature:funhouse-engine-kotlin`)
Path: `/Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine-kotlin`

### 6.4.1 `ConcurrencyHelpersIos.kt`
```kotlin
package com.funhouse.feature.funhouseenginekotlin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

actual class GcConcurrentMap<K, V> actual constructor() {
    private val map = mutableMapOf<K, V>()
    actual fun put(key: K, value: V): V? = gcSynchronized(this) { map.put(key, value) }
    actual fun remove(key: K): V? = gcSynchronized(this) { map.remove(key) }
    actual operator fun get(key: K): V? = gcSynchronized(this) { map[key] }
    actual operator fun set(key: K, value: V) { gcSynchronized(this) { map[key] = value } }
    actual fun clear() = gcSynchronized(this) { map.clear() }
    actual fun containsKey(key: K): Boolean = gcSynchronized(this) { map.containsKey(key) }
    actual val values: Collection<V> get() = gcSynchronized(this) { map.values.toList() }
    actual val entries: Set<Map.Entry<K, V>> get() = gcSynchronized(this) { map.entries.toSet() }
    actual fun forEach(action: (Map.Entry<K, V>) -> Unit) = gcSynchronized(this) { map.entries.forEach(action) }
    actual fun getOrPut(key: K, defaultValue: () -> V): V = gcSynchronized(this) { map.getOrPut(key, defaultValue) }
    actual operator fun iterator(): Iterator<Map.Entry<K, V>> = gcSynchronized(this) { map.toMap().iterator() }
}

actual class GcQueue<T> actual constructor() {
    private val channel = Channel<T>(Channel.UNLIMITED)
    actual fun put(element: T) { channel.trySend(element) }
    actual suspend fun take(): T = channel.receive()
    actual fun poll(): T? = channel.tryReceive().getOrNull()
    actual fun clear() {
        while (true) {
            val res = channel.tryReceive()
            if (res.isFailure || res.isClosed) break
        }
    }
}

actual class GcThreadLocal<T> actual constructor() {
    private var value: T? = null
    actual fun get(): T? = value
    actual fun set(value: T) { this.value = value }
}

actual fun <T> gcThreadLocal(initial: () -> T): GcThreadLocal<T> =
    GcThreadLocal<T>().apply { set(initial()) }

actual class GcThreadRef(val job: Job? = null) {
    actual fun interrupt() { job?.cancel() }
}

actual fun GcThreadRef.isCurrentThread(): Boolean = true

actual fun gcThread(name: String, block: suspend () -> Unit): GcThreadRef {
    val job = CoroutineScope(Dispatchers.Default).launch {
        try { block() } catch (t: Throwable) {}
    }
    return GcThreadRef(job)
}

// Do NOT use objc_sync_enter/exit on Kotlin objects: they are not Objective-C objects, and the bridge
// wrapper is not a reliable lock identity. Use one process-wide reentrant lock from atomicfu instead.
// Coarse, but safe: the engine only locks short sections, and the lock is reentrant for nested calls.
private val engineLock = kotlinx.atomicfu.locks.reentrantLock()

actual fun <R> gcSynchronized(lock: Any, block: () -> R): R {
    engineLock.lock()
    try {
        return block()
    } finally {
        engineLock.unlock()
    }
}

actual fun getLocalIps(): List<String> = emptyList()
actual fun isHostPortAvailable(ip: String, port: Int): Boolean = false
actual fun scanSubnetForHost(myIp: String, networkPort: Int, onHostDiscovered: (String) -> Unit) {}
actual fun gcSleep(ms: Long) { Thread.sleep(ms) } // kotlin.Thread shim from :shared:common (chunked usleep)
actual fun isWebPlatform(): Boolean = false
```
> [!IMPORTANT]
> - Add atomicfu through the catalog (rule 8): in `gradle/libs.versions.toml` add `atomicfu = "<latest from Maven Central>"` and `kotlinx-atomicfu = { module = "org.jetbrains.kotlinx:atomicfu", version.ref = "atomicfu" }`, then `iosMain.dependencies { implementation(libs.kotlinx.atomicfu) }` in `feature/funhouse-engine-kotlin/build.gradle.kts`.
> - `gcSleep`: `Thread.sleep(ms)` resolves to the `kotlin.Thread` shim from `:shared:common`. Do not call `usleep` with values >= 1 s directly.
> - `GcThreadLocal` is a plain field and `isCurrentThread()` always returns true. This is only correct while multiplayer is stubbed (one player). `GengameKotlin` uses `gcThreadLocal { 1 }` for `activePlayerIndex` and `isCurrentThread()` for player-thread cleanup, so real thread identity is required before iOS multiplayer is enabled.

### 6.4.2 `DiscoveryHelperIos.kt` & `GameSocketIos.kt`
Match the `desktopMain` stubs initially to allow single-player games (*Island*, *FunHouse*, *Space Station Aegis*) to run immediately.

When iOS multiplayer is implemented later: advertise and browse `_funhouse-game._tcp` (the Android NSD type in `androidMain/.../NsdHelper.kt`), add the local network keys from 4.3.E, and replace `GcThreadLocal` / `isCurrentThread()` with real per-thread implementations.

---

## 6.5 Step 4: `:composeApp` Module & `iosApp` Xcode Project Wrapper
Path: `/Users/luizvaldetaro/valdetaro/FunHouse/composeApp`

### 6.5.1 Update `composeApp/build.gradle.kts`
```kotlin
kotlin {
    // Android, JVM desktop, WasmJs...  (no iosX64 — BUG-011)
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // No export(...): composeApp declares :shared:common as `implementation`, and export()
            // requires an `api` dependency (the build fails otherwise). Swift only calls
            // MainViewController(), so nothing needs to be exported.
        }
    }
}
```

Also add an iOS version code to the generated `CommonConfig` (in the `generateCommonConfig` task), so the iOS build never hardcodes the version:
```kotlin
// inside the generated object CommonConfig { ... }
const val iosVersionCode = ${vCode*10+7}L
```
Keep `CFBundleShortVersionString` / `CFBundleVersion` in `Info.plist` in sync with `libs.versions.toml` (`versionName`, `versionCode`); see 6.5.3 (XcodeGen reads them from build settings).

### 6.5.2 Create `composeApp/src/iosMain/kotlin/Main.kt`
```kotlin
package com.gepetto.funhouse

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.foundation.isSystemInDarkTheme
import platform.UIKit.UIViewController
import platform.Foundation.*
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.Constants
import com.gepetto.gamescollection.CommonConfig
import com.gepetto.funhouse.models.installAssetFiles
import com.gepetto.funhouse.ui.main.MainView
import club.gepetto.utils.GcAppInfo
import club.gepetto.GcLog
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.gepetto.funhouse.intentprocessors.FunHouseIntentProcessor
import java.io.File

fun MainViewController(): UIViewController {
    // 1. Guard Koin from re-initialization crashes
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(module {
                single { FunHouseIntentProcessor() }
            })
        }
    }

    // 2. Prepare iOS Sandbox File Directory
    val fileManager = NSFileManager.defaultManager
    val docUrl = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).first() as NSURL
    val basePath = docUrl.path ?: ""
    val gamePath = "$basePath/${Constants.GAMES_FOLDER}"
    fileManager.createDirectoryAtPath(gamePath, withIntermediateDirectories = true, attributes = null, error = null)

    // 3. Initialize AppData with File instance to prevent ClassCastException
    AppData.appPackage = "com.gepetto.gamescollection"
    AppData.appName = "FunHouse"
    AppData.packageFolder = basePath
    AppData.packageFolderFile = File(basePath)
    AppData.gameFolder = Constants.GAMES_FOLDER
    AppData.gameFolderFile = File(gamePath)

    val vCode = CommonConfig.iosVersionCode   // generated; never hardcode (was 163, app is now 164)
    GcAppInfo.versionName = CommonConfig.versionName
    GcAppInfo.versionCode = vCode
    GcAppInfo.appPackageFolder = basePath     // iOS default is "" — must be set
    GcAppInfo.releaseVersion = true

    AppData.version = "${CommonConfig.versionName}/ios"
    AppData.versionCode = vCode
    AppData.releaseVersion = true
    AppData.secretGamesEnabled = false

    GcLog.plant(GcLog.DebugTree())
    installAssetFiles()

    return ComposeUIViewController {
        AppData.darkMode = isSystemInDarkTheme()
        MainView()
    }
}
```

### 6.5.3 Xcode Project Generation (XcodeGen)
The Rev 4 Python script did not work: it wrote `iOSApp.swift` and `Info.plist` but never wrote `project.pbxproj`, so Xcode could not open the project. Do not hand-write `project.pbxproj`. Use **XcodeGen** (`brew install xcodegen`) with a checked-in spec. Commit the spec and sources; add the generated `iosApp/iosApp.xcodeproj/` to `.gitignore` (rule 10) and regenerate it with `xcodegen`.

Files to create:
```
iosApp/
  project.yml
  iosApp/
    iOSApp.swift
    Info.plist
    PrivacyInfo.xcprivacy      (see 4.3.E.2)
    Assets.xcassets/AppIcon.appiconset/   (1024x1024 PNG, no transparency)
```

`iosApp/project.yml`:
```yaml
name: iosApp
options:
  bundleIdPrefix: com.gepetto
  deploymentTarget:
    iOS: "16.0"
settings:
  base:
    MARKETING_VERSION: "2.1.64"        # = versionName in gradle/libs.versions.toml
    CURRENT_PROJECT_VERSION: "1647"    # = CommonConfig.iosVersionCode
targets:
  iosApp:
    type: application
    platform: iOS
    sources: [iosApp]
    info:
      path: iosApp/Info.plist
      properties:
        CFBundleDisplayName: FunHouse
        CFBundleShortVersionString: $(MARKETING_VERSION)
        CFBundleVersion: $(CURRENT_PROJECT_VERSION)
        ITSAppUsesNonExemptEncryption: false
        UILaunchScreen: {}
        UISupportedInterfaceOrientations:
          - UIInterfaceOrientationPortrait
          - UIInterfaceOrientationLandscapeLeft
          - UIInterfaceOrientationLandscapeRight
        UISupportedInterfaceOrientations~ipad:
          - UIInterfaceOrientationPortrait
          - UIInterfaceOrientationPortraitUpsideDown
          - UIInterfaceOrientationLandscapeLeft
          - UIInterfaceOrientationLandscapeRight
    settings:
      base:
        PRODUCT_BUNDLE_IDENTIFIER: com.gepetto.gamescollection
        TARGETED_DEVICE_FAMILY: "1,2"
        FRAMEWORK_SEARCH_PATHS: "$(SRCROOT)/../composeApp/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)"
        OTHER_LDFLAGS: ["-framework", "ComposeApp"]
        ENABLE_USER_SCRIPT_SANDBOXING: NO
    preBuildScripts:
      - name: Compile Kotlin Framework
        basedOnDependencyAnalysis: false
        script: |
          cd "$SRCROOT/.."
          ./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```
> [!NOTE]
> `CURRENT_PROJECT_VERSION` must increase for every App Store upload. Update both version settings whenever `libs.versions.toml` changes (or add a Gradle task that rewrites them).

`iosApp/iosApp/iOSApp.swift`:
```swift
import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView().ignoresSafeArea(.all)
        }
    }
}

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```
`ignoresSafeArea(.all)` hands all insets to Compose. Screens must then apply `WindowInsets.safeDrawing` (or `systemBarsPadding()` / `displayCutoutPadding()`, which common code already uses) and `imePadding()` on text-game input fields, so the notch, home indicator and keyboard do not cover content. Verify this in Phase 4.

Generate and build:
```bash
cd /Users/luizvaldetaro/valdetaro/FunHouse/iosApp
xcodegen
xcodebuild -project iosApp.xcodeproj -scheme iosApp \
           -destination 'platform=iOS Simulator,name=iPhone 17' \
           -configuration Debug -derivedDataPath build build
xcrun simctl install booted build/Build/Products/Debug-iphonesimulator/iosApp.app
xcrun simctl launch booted com.gepetto.gamescollection
```

---

## 6.6 Step 5: Resource & File Installation Pipeline

FunHouse relies on game data files (CSV, JSON, Markdown, text files) located in `composeResources/files/`:
- In Desktop/Android, `installAssetFiles()` copies them to `AppData.gameFolderFile`.
- On iOS, `PlatformHelpersIos.installFile()` reads each file with `Res.readBytes("files/<name>")` (fallback `drawable/<name>`) and writes it into the iOS sandbox `Documents/<Constants.GAMES_FOLDER>/` directory. It must not guess bundle paths (see 6.2.3).
- All 96 data files live in one place: `shared/common/src/commonMain/composeResources/files/`.
- Verification (Phase 4): after first launch, list the sandbox folder and confirm the files exist:
  ```bash
  ls "$(xcrun simctl get_app_container booted com.gepetto.gamescollection data)/Documents/"*
  ```
- Because `AppData.gameFolderFile` is an instance of `java.io.File(gamePath)`, calls like:
  ```kotlin
  val licenseFile = File(AppData.gameFolderFile as File, game.licenseFile!!.fileName)
  ```
  succeed with zero modifications to common game logic.

---

## 7. Phase-by-Phase Execution Plan for Agents

Any agent picking up this plan can execute the phases sequentially using these exact steps:

### Phase 0: Shared Libraries (`GepettoLabs`, then named `gepetto-utils`) [COMPLETED & PUBLISHED]
> Historical record. Paths below refer to the old `~/valdetaro/gepetto-utils` layout; today use `~/valdetaro/GepettoLabs/<lib>` and `GepettoLabs/publishlocal`.
- [x] 0.0: Update `gepetto-utils/gradle/libs.versions.toml` with `ktor-client-darwin` and sync versions (`circum:2.1.1`, `gepetto-utils:2.1.1`, `gclog:0.1.1`, `gcadslib:0.4.1`).
- [x] 0.1: Add iOS targets (`iosArm64`, `iosSimulatorArm64`) and implement `CircumIntentProcessorFunctions.ios.kt` in `gepetto-utils/circum`.
- [x] 0.2: Add iOS targets, `applyDefaultHierarchyTemplate()`, `ktor-client-darwin`, and implement 11 actual files (`PlatformFile.ios.kt`, `PlatformBitmap.ios.kt`, `PlatformHttpClient.ios.kt`, `GcCurrentTimeMillis.ios.kt`, `Actuals.ios.kt`, `GcQrCodeScanner.ios.kt`, `ImageActuals.ios.kt`, `WebComposeUtils.ios.kt`, `Dispatcher.ios.kt`, `Utils.ios.kt`, `GcAppInfo.ios.kt`) in `gepetto-utils/gepetto-utils`.
- [x] 0.3: Add iOS targets and implement `GcLog.kt` (in `package club.gepetto`) with regex format specifiers and safe `NSLog("%s", ...)` in `gepetto-utils/gclog`.
- [x] 0.4: Add iOS targets and implement `Actuals.ios.kt` (with persistent `NSUserDefaults` first-run) and `ActualsUi.ios.kt` in `gepetto-utils/ads-lib`.
- [x] 0.5: Run `./gradlew --no-daemon publishToMavenLocal` inside `/Users/luizvaldetaro/valdetaro/gepetto-utils` and verify artifacts under `~/.m2/repository/club/gepetto/`.
- [x] 0.6: Verify regression-free compilation: `./gradlew --no-daemon compileDebugKotlinAndroid compileKotlinDesktop compileKotlinWasmJs` and `./gradlew --no-daemon compileKotlinIosSimulatorArm64 compileKotlinIosArm64`.

### Phase 1: FunHouse `:shared:common` Module & Shims
- [ ] 1.1: Add iOS targets (`iosArm64`, `iosSimulatorArm64` only) and `-Xallow-kotlin-package` to `FunHouse/shared/common/build.gradle.kts`.
- [ ] 1.2: Implement `java.io.File` (binary-safe `readBytes`/`copyTo`), `BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`, `IOException`, `PrintWriter`, `Serializable` shims in `src/iosMain/kotlin/java/io/`.
- [ ] 1.3: Implement `java.util.Random` (seed honored) and `Locale` in `src/iosMain/kotlin/java/util/`.
- [ ] 1.4: Implement `Thread.sleep` (chunked), `InterruptedException(String)`, `System` (with `err`), and full `Math` (incl. `toRadians`, Long/Float/Double overloads) in `src/iosMain/kotlin/kotlin/`.
- [ ] 1.5: Copy `kotlin/collections/CollectionsCompat.kt` (`removeIf`) and `kotlinx/coroutines/channels/TickerShim.kt` (`ticker`) from `wasmJsMain`. Do NOT copy `DispatchersIO.kt`.
- [ ] 1.6: Implement `android/content/Context`, `MediaMocks`, `SuppressLint`, and `LocalContext` in `src/iosMain/kotlin/android/` and `androidx/`.
- [ ] 1.7: Implement `PlatformHelpersIos.kt` (`installFile` via `Res.readBytes`, lazy `AVSpeechSynthesizer`), `TimeHelpersIos.kt`, `BackHandlerIos.kt`, `CacheHelper.ios.kt`, and `BaseKotlinGameSoundsIos.kt`.
- [ ] 1.8: Diff the public declarations in `src/wasmJsMain/kotlin/{java,kotlin,kotlinx,android,androidx}` against `src/iosMain/kotlin/`; every shim used by common code must exist on iOS.
- [ ] 1.9: Verify compilation: `./gradlew :shared:common:compileKotlinIosSimulatorArm64`. Then, as an early check of the shims, compile the shim-heavy modules: `./gradlew :feature:castle-kotlin:compileKotlinIosSimulatorArm64 :feature:classic-arcades:compileKotlinIosSimulatorArm64 :feature:colossal-cave-adventure-kotlin:compileKotlinIosSimulatorArm64 :feature:tetric:compileKotlinIosSimulatorArm64` (needs the iOS targets from 2.1 in those four modules).
- [ ] 1.10: Regression check (rule 9): `./gradlew :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinDesktop :composeApp:compileKotlinWasmJs`.

### Phase 2: Feature Modules (20 Games)
- [ ] 2.1: Add iOS targets (`iosArm64`, `iosSimulatorArm64`) to all 19 commonMain-only feature `build.gradle.kts` files.
- [ ] 2.2: Add iOS targets to `feature/funhouse-engine-kotlin/build.gradle.kts`, plus atomicfu (via the catalog) in `iosMain`.
- [ ] 2.3: Implement `ConcurrencyHelpersIos.kt` (atomicfu lock, not `objc_sync_enter`), `DiscoveryHelperIos.kt`, and `GameSocketIos.kt` (stubs) in `feature/funhouse-engine-kotlin`.
- [ ] 2.4: Verify compilation: `./gradlew compileKotlinIosSimulatorArm64`. If a game fails, fix the shim, not the game.
- [ ] 2.5: Regression check: Android, Desktop and WasmJs compile tasks (as in 1.10) and existing tests (`:feature:funhouse-engine-kotlin:desktopTest`, `:feature:wander-engine-kotlin:desktopTest`).

### Phase 3: `:composeApp` & `iosApp` Xcode Wrapper
- [ ] 3.1: Add iOS framework target to `FunHouse/composeApp/build.gradle.kts` (no `export(...)`), and add `iosVersionCode` to `generateCommonConfig`.
- [ ] 3.2: Implement `composeApp/src/iosMain/kotlin/Main.kt` with safe Koin initialization, `File(gamePath)`, `CommonConfig.iosVersionCode`, and `GcAppInfo.appPackageFolder`.
- [ ] 3.3: Create `iosApp/project.yml`, `iOSApp.swift`, `Info.plist`, `PrivacyInfo.xcprivacy`, `Assets.xcassets`; run `xcodegen`; add `iosApp/iosApp.xcodeproj/` and `iosApp/build/` to `.gitignore`.
- [ ] 3.4: Build standalone debug framework: `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`. (`embedAndSignAppleFrameworkForXcode` only works when Xcode runs it, because it reads Xcode environment variables; check it through the `xcodebuild` build in 4.2.)

### Phase 4: Simulator Launch & Gameplay Verification
- [ ] 4.1: Boot simulator: `xcrun simctl boot "iPhone 17" && open -a Simulator`.
- [ ] 4.2: Build and run `iosApp` via `xcodebuild` targeting booted simulator.
- [ ] 4.3: Verify game category navigation (Skill, Arcade, Chatbots, Chance, Adventure).
- [ ] 4.4: Verify test games:
  - Compose games: Blackjack, Chess, Slot Machine, Classic Arcades.
  - Text games: Eliza, Adventure, FunHouse, Wander, Castle.
- [ ] 4.5: Verify dark mode toggle (`Cmd + Shift + A`) and landscape orientation (`Cmd + Arrow`).
- [ ] 4.6: Verify the game files were installed in the sandbox (command in 6.6), and that seeded games (Chimaera, Wizard's Castle) start.
- [ ] 4.7: Verify safe areas and the software keyboard: text-game input stays visible above the keyboard on iPhone 17 and on an iPad simulator.
- [ ] 4.8: Run each text game (Eliza, Adventure, Castle, Dinkum, Chimaera, Wizard's Castle, FunHouse, Wander) with the same commands as the desktop baseline and compare the output.
- [ ] 4.9: Hide any game that does not work on iOS before Phase 6 (Guideline 2.1).

### Phase 5: Google Mobile Ads (AdMob) iOS Integration (Monetization)
- [ ] 5.1: Add Google Mobile Ads SDK (`Google-Mobile-Ads-SDK`) via Swift Package Manager to `iosApp.xcodeproj`.
- [ ] 5.2: Configure `GADApplicationIdentifier` in `iosApp/Info.plist`.
- [ ] 5.3: Add `NSUserTrackingUsageDescription` (ATT prompt) and `SKAdNetworkItems` to `iosApp/Info.plist`.
- [ ] 5.4: Implement the UMP consent form (EEA/UK), then the ATT authorization trigger, in `iOSApp.swift`.
- [ ] 5.5: Wire `AdBannerView` bridge into `iosMain` and test with Google's iOS Test Ad Unit ID.

### Phase 6: App Store Connect & Distribution Preparation
- [ ] 6.1: Create Apple Developer Account ($99/year individual enrollment).
- [ ] 6.2: Create App record in App Store Connect with bundle ID `com.gepetto.gamescollection`.
- [ ] 6.3: Capture simulator screenshots (`Cmd + S`) for 6.9" iPhone (iPhone 17 Pro Max) and 13" iPad.
- [ ] 6.4: Complete Age Rating questionnaire (declaring Simulated Gambling for Casino games; ratings are 4+/9+/13+/16+/18+).
- [ ] 6.4b: Confirm `PrivacyInfo.xcprivacy` is in the app target and matches the APIs in use (Xcode → Product → Archive → Generate Privacy Report).
- [ ] 6.5: Complete App Store Privacy questionnaire: Declare Tracking/Identifiers/Diagnostics for Google AdMob.
- [ ] 6.6: Publish Privacy Policy URL.
- [ ] 6.7: Create release archive via Xcode and submit to TestFlight.

---

## 8. Living Document Changelog

| Date | Author / Agent | Summary of Changes |
|---|---|---|
| 2026-09-17 | Initial Agent | Rev 1: Created living document `IOS_PORT_PLAN.md`. Detailed tools, testing without physical device, Apple Developer setup, App Store approval guidelines (simulated gambling), module technical specs, and 6-phase execution plan. |
| 2026-09-17 | Initial Agent | Rev 2: Clarified Google Mobile Ads (AdMob) iOS SDK support, explaining native Apple SDK vs Android AAR, phased integration, ATT prompt, and Info.plist requirements. |
| 2026-09-17 | Initial Agent | Rev 3: Full Google AdMob iOS integration added into the plan: complete code samples (stubs vs SPM/UIKitView bridge), ATT authorization Swift code, GADApplicationIdentifier crash prevention, SKAdNetworkItems, App Store Privacy Nutrition Labels, and dedicated Phase 5 execution checklist. |
| 2026-09-18 | Review Agent | Rev 4: Comprehensive audit & update: (1) Synchronized with recent JDK 21 / Kotlin 2.4.20 / Compose 1.12.0 migrations; (2) Fixed `gclog` package mismatch (`club.gepetto`); (3) Corrected `circumIntentProcessor` signature with `@Composable` and `koinInject`; (4) Supplied complete expect/actual stubs for `ads-lib` matching `ExpectationsUi.kt`; (5) Documented full `gepetto-utils` iOS actuals (`PlatformFile`, `createPlatformHttpClient` with `ktor-client-darwin`, `PlatformBitmap`); (6) Reordered Phase 0 build order (`circum` -> `gepetto-utils` -> `gclog` -> `ads-lib`); (7) Added missing `java.io.*` and `Thread` shims to allow 20 game modules to compile; (8) Fixed runtime `ClassCastException` on `AppData.gameFolderFile` by using `File(gamePath)`; (9) Fixed Koin re-initialization crash with `GlobalContext.getOrNull()`; (10) Added automated Python generator for `iosApp.xcodeproj`. |
| 2026-09-18 | Execution Agent | Rev 5: Phase 0 Completed & Verified. Added modern Apple iOS targets (`iosArm64`, `iosSimulatorArm64`) to all 4 libraries in `gepetto-utils` (`circum:2.1.1`, `gepetto-utils:2.1.1`, `gclog:0.1.1`, `gcadslib:0.4.1`), created all 15 native iOS actual files, published all klibs and artifacts to `mavenLocal()`, verified existing Android/Desktop/WasmJs and iOS targets cleanly without regressions. Advanced roadmap to Phase 1. |
| 2026-11-13 | Audit Agent | Rev 6: Audit against the codebase. (1) Library paths moved to `GepettoLabs/` (4 standalone projects); corrected build order (gclog has no deps); iOS libs confirmed on Maven Central. (2) Removed `iosX64` from all code samples (contradicted BUG-011). (3) Completed iOS shims: `InterruptedException(String)`, `System.err`, full `Math`, `removeIf`, `ticker`, seeded `Random`, binary-safe `File.readBytes/copyTo`, chunked `Thread.sleep`; added usage table and wasm-parity rule. (4) `installFile` now uses `Res.readBytes` (old bundle paths missed `composeResources/`). (5) Removed invalid `export(project(":shared:common"))`. (6) Version code from generated `CommonConfig.iosVersionCode`; set `GcAppInfo.appPackageFolder`. (7) `gcSynchronized` uses atomicfu lock, not `objc_sync_enter`. (8) Bonjour type `_funhouse-game._tcp` to match Android. (9) Replaced non-functional Python generator with XcodeGen `project.yml`. (10) Added `PrivacyInfo.xcprivacy`, UMP consent, new App Store age ratings and screenshot sizes; Guideline 4.7 reference corrected. (11) Toolchain: Xcode 27.2, Kotlin 2.4.21, app 2.1.64/164. (12) Expanded Phase 1–4 checklists. |

---

## 9. Bug, Blocker & Issue Tracker

| ID | Module / Component | Description | Status | Workaround / Resolution |
|---|---|---|---|---|
| BUG-001 | `gepetto-utils` | iOS targets not declared in `gepetto-utils` library | Resolved in Phase 0 | Implemented `iosArm64()` and `iosSimulatorArm64()` in all 4 libraries and published to `mavenLocal()` (now also on Maven Central). |
| BUG-002 | `funhouse-engine-kotlin` | Platform-specific WebSocket & Concurrency code on JVM/Android | Open (Phase 2) | Implement Coroutine Channel queue, usleep, `objc_sync_enter/exit`, and stubs for discovery/sockets. |
| BUG-003 | App Store Review | Risk of rejection due to undeclared casino games | Documented (Phase 6) | Declare "Simulated Gambling" in Age Rating questionnaire (12+/17+ rating). |
| BUG-004 | App Store Review | Risk of rejection if "Tetric" infringes Tetris trademark | Documented (Phase 6) | Ensure `AppData.secretGamesEnabled = false` for release builds. |
| BUG-005 | Google AdMob iOS | Crash on launch if GADApplicationIdentifier is missing | Documented (Phase 5) | Ensure valid GADApplicationIdentifier key is present in Info.plist. |
| BUG-006 | `circum` | `circumIntentProcessor` signature mismatch | Resolved in Rev 4 | Use `@Composable actual inline fun <reified CIP : CircumViewModel> circumIntentProcessor(initialState: Any?, initialCommand: Any?): CIP` with `koinInject`. |
| BUG-007 | `shared:common` | 12+ games fail to compile due to missing `java.io.File` and `Thread.sleep` | Resolved in Rev 4 | Added iOS shims in `src/iosMain/kotlin/` with compiler flag `-Xallow-kotlin-package`. |
| BUG-008 | `composeApp` | `ClassCastException` on `AppData.gameFolderFile as File` | Resolved in Rev 4 | Set `AppData.gameFolderFile = File(gamePath)` using the iOS `File` shim instead of raw `String`. |
| BUG-009 | `composeApp` | `KoinAppAlreadyStartedException` when SwiftUI recreates `MainViewController` | Resolved in Rev 4 | Guard `startKoin` with `if (GlobalContext.getOrNull() == null)`. |
| BUG-010 | `gclog` | Package mismatch between `club.gepetto.gclog` and common `club.gepetto` | Resolved in Rev 4 | Use `package club.gepetto` for iOS actuals. |
| BUG-011 | Modern KMP / Compose 1.12.0 | `iosX64` targets fail dependency resolution | Resolved in Phase 0 | Target modern Apple Silicon: `iosSimulatorArm64` (simulator) and `iosArm64` (device). |
| BUG-012 | `gepetto-utils` / coroutines | ~~`Dispatchers.IO` is internal in Kotlin/Native coroutines~~ — incorrect: `Dispatchers.IO` is public on Kotlin/Native (needs `import kotlinx.coroutines.IO`). | Resolved in Phase 0 (no change needed) | `Dispatchers.Default` for iOS `ioDispatcher` works; switching to `Dispatchers.IO` is optional. Do not port the wasm `DispatchersIO.kt` shim to iOS. |
| BUG-013 | `shared:common` shims | Rev 5 shims miss APIs used by games: `InterruptedException(String)`, `System.err`, `Math.toRadians` + overloads, `removeIf`, `ticker`. Phase 2 would not compile. | Fixed in plan (Rev 6) | See 6.2.2 usage table; keep iOS shims in parity with `wasmJsMain`. |
| BUG-014 | `shared:common` | `installFile` bundle paths left out `composeResources/`, so no game file would be installed (silent `return`). | Fixed in plan (Rev 6) | Use `Res.readBytes("files/$name")`. |
| BUG-015 | `composeApp` | `export(project(":shared:common"))` fails: export requires an `api` dependency. | Fixed in plan (Rev 6) | Removed `export`. |
| BUG-016 | `shared:common` shims | `java.util.Random(seed)` ignored the seed; `File.readBytes()`/`copyTo()` went through text and corrupted binary files. | Fixed in plan (Rev 6) | Seeded `kotlin.random.Random`; NSData / NSFileManager copies. |
| BUG-017 | `funhouse-engine-kotlin` | `objc_sync_enter` on Kotlin objects is not a reliable lock. | Fixed in plan (Rev 6) | atomicfu reentrant lock. |
| BUG-018 | `iosApp` | Rev 4 Python generator never wrote `project.pbxproj`. | Fixed in plan (Rev 6) | XcodeGen `project.yml`. |
| BUG-019 | App Store | Missing privacy manifest (NSUserDefaults, file timestamps are required-reason APIs). | Fixed in plan (Rev 6) | `PrivacyInfo.xcprivacy` in 4.3.E.2. |

*(Agents executing this plan: Add new entries above whenever a bug or obstacle is encountered during implementation)*
