# iOS Port Plan: FunHouse Game Collection (Living Document)

> **Document Status**: Living Roadmap & Technical Specification  
> **Target Application**: FunHouse Game Collection (`/Users/luizvaldetaro/valdetaro/FunHouse`)  
> **Workspace**: `/Users/luizvaldetaro/valdetaro`  
> **Document Location**: `.agents/IOS_PORT_PLAN.md`  
> **Last Updated**: 2026-09-17 (Rev 2 — Google AdMob iOS SDK clarification added)  
> **Current Phase**: Phase 0 (Environment Setup & Shared Library Prerequisites)

---

## Table of Contents
1. [Executive Summary & Current Architectural State](#1-executive-summary--current-architectural-state)
2. [Prerequisites & Development Tools Setup (Zero iOS Experience Guide)](#2-prerequisites--development-tools-setup-zero-ios-experience-guide)
3. [Testing Strategy Without a Physical iOS Device](#3-testing-strategy-without-a-physical-ios-device)
4. [Apple Developer Account & App Store Approval Process](#4-apple-developer-account--app-store-approval-process)
5. [Architectural Dependency Graph & Strategy](#5-architectural-dependency-graph--strategy)
6. [Detailed Technical Specification by Module](#6-detailed-technical-specification-by-module)
   - 6.1 [Step 0: Shared Libraries (`gepetto-utils`) KMP iOS Support](#61-step-0-shared-libraries-gepetto-utils-kmp-ios-support)
   - 6.2 [Step 1: FunHouse `:shared:common` Module](#62-step-1-funhouse-sharedcommon-module)
   - 6.3 [Step 2: FunHouse 20 Feature Game Modules](#63-step-2-funhouse-20-feature-game-modules)
   - 6.4 [Step 3: FunHouse Engine Networking & Concurrency (`feature:funhouse-engine-kotlin`)](#64-step-3-funhouse-engine-networking--concurrency-featurefunhouse-engine-kotlin)
   - 6.5 [Step 4: `:composeApp` Module & `iosApp` Xcode Wrapper](#65-step-4-composeapp-module--iosapp-xcode-wrapper)
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
2. `:shared:common`: Domain models (`Game`, `AppData`), sound player interfaces, file utilities, and common UI helpers.
3. 20 feature modules (`:feature:*`): Each housing self-contained game logic and UI.

The app also depends on four shared KMP libraries in `~/valdetaro/gepetto-utils`:
- `club.gepetto:gepetto-utils`
- `club.gepetto:gclog`
- `club.gepetto:gcadslib`
- `club.gepetto:circum`

### Current Porting Obstacle
Currently, neither `gepetto-utils` nor `FunHouse` declares Apple iOS targets (`iosArm64`, `iosSimulatorArm64`, `iosX64`). To run on iOS:
1. Shared libraries must build for iOS targets and publish to `mavenLocal()`.
2. FunHouse Gradle configurations must add iOS targets.
3. Native Apple framework bindings (`AVSpeechSynthesizer`, `NSFileManager`, `AVAudioPlayer`, `NSCalendar`) must be provided for `shared:common` and `funhouse-engine-kotlin`.
4. An `iosApp` Xcode project shell must be created to wrap the Compose Multiplatform UI (`ComposeUIViewController`) into a native iOS application.

---

## 2. Prerequisites & Development Tools Setup (Zero iOS Experience Guide)

Because you have 0 experience developing for iOS and do not own an iOS device, here is what you need to know about your development environment on macOS:

### 2.1 Hardware & macOS Environment
- **Machine**: You are running macOS on an Apple Silicon / Intel Mac.
- **Xcode**: Apple's official IDE and toolchain for iOS.
  - Verification on your machine confirms Xcode is installed at `/Applications/Xcode.app` (Xcode 27.0) and active developer directory points to `/Applications/Xcode.app/Contents/Developer`.
  - iOS Simulator Runtime: **iOS 26.5** is installed and ready.

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
- Localhost and local Wi-Fi networking (WebSockets connect directly via Mac network interface).

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
1. **Via Gradle**:
   ```bash
   ./gradlew :composeApp:compileKotlinIosSimulatorArm64
   ```
2. **Via Xcode (Simplest GUI workflow)**:
   - Double-click `/Users/luizvaldetaro/valdetaro/FunHouse/iosApp/iosApp.xcodeproj`.
   - At the top bar, select the target: `iosApp` -> `iPhone 17 (Simulator)`.
   - Press **Cmd + R** (or click the **Play** button).
   - Xcode will trigger Gradle in the background, embed the framework, launch the simulator, and attach the debugger.

### 3.4 Key Simulator Shortcuts for Testing
- **Toggle Dark / Light Mode**: `Cmd + Shift + A`
- **Rotate Device (Landscape / Portrait)**: `Cmd + Left Arrow` or `Cmd + Right Arrow`
- **Toggle Virtual Keyboard**: `Cmd + K`
- **Home Screen**: `Cmd + Shift + H`
- **Take App Store Screenshot**: `Cmd + S` (Saves directly to your Mac Desktop with pixel-perfect resolution).

### 3.5 When is a Physical Device Actually Needed?
For FunHouse (a collection of 2D Compose and text games), a physical device is **optional** even up to App Store submission. Apple does not require you to own a device.
However, before launching publicly, you can distribute a test build via **Apple TestFlight** (included with the Apple Developer Program). You can invite friends, family, or beta testers with an iPhone to test and provide feedback with one click.

---

## 4. Apple Developer Account & App Store Approval Process

To publish on the Apple App Store, Apple requires registration with the Apple Developer Program and compliance with App Store Review Guidelines.

### 4.1 Step 1: Enrolling in the Apple Developer Program
1. **Apple ID**: You need a standard Apple ID (the account you use for iCloud / Mac App Store) with Two-Factor Authentication (2FA) enabled.
2. **Enrollment**:
   - Option A (Easiest): Open the **Apple Developer app** on your Mac (install from Mac App Store), sign in, and tap **Enroll**.
   - Option B: Visit [developer.apple.com/programs/enroll](https://developer.apple.com/programs/enroll/).
3. **Account Types**:
   - **Individual ($99 USD / year)**:
     - Recommended for getting started immediately.
     - Fast approval (often within 24–48 hours).
     - The App Store developer name will be your personal legal name.
   - **Organization ($99 USD / year)**:
     - Displays a studio name (e.g. "Gepetto").
     - Requires a free **D-U-N-S Number** from Dun & Bradstreet (takes 1–2 weeks).
     - You can start as an Individual and convert to Organization later if desired.

### 4.2 Step 2: Certificates, Identifiers, and Signing
Once enrolled:
1. **Bundle Identifier**: Register an explicit App ID in the developer portal:
   - Suggested: `com.gepetto.gamescollection` (matching Android) or `club.gepetto.funhouse`.
2. **Xcode Automatic Signing**:
   - In Xcode -> **Settings** -> **Accounts**, click `+` and sign in with your Apple ID.
   - In the `iosApp` project settings -> **Signing & Capabilities**, check **"Automatically manage signing"** and select your Team.
   - Xcode will generate development certificates and provisioning profiles automatically.

### 4.3 Step 3: App Store Approval Guidelines & FunHouse Specific Gotchas

Apple reviews every app submission with human reviewers. To ensure approval on the first attempt:

#### A. Guideline 4.7: Mini-Apps & Game Collections
- FunHouse contains 20 classic games in one binary. Apple explicitly allows game collections and retro game engines under Guideline 4.7, provided all software inside complies with privacy and content rules, and does not require third-party app stores.
- Every game in the collection must be fully functional and stable.

#### B. The "Simulated Gambling" Age Rating Gotcha (CRITICAL)
- FunHouse includes **Blackjack**, **Craps**, **Roulette**, **Slot Machine**, and **Poker**.
- In the App Store Connect Age Rating questionnaire, you **MUST** declare:
  - **Simulated Gambling**: Answer **"Frequent / Intense"** (or "Infrequent/Mild" depending on usage).
  - Apple will automatically assign a **12+** or **17+** age rating.
  - **Crucial Note**: The app description must clearly state:
    > *"All casino games (Blackjack, Craps, Roulette, Slot Machine, Poker) are for entertainment purposes only. The app uses virtual chips and credits. No real money gambling or real prizes are offered or won."*
  - Failure to declare simulated gambling will result in immediate rejection under Guideline 2.3 (Accurate Metadata).

#### C. Intellectual Property & Copyrights (Guideline 5.2)
- **Tetric**: Keep the game hidden by default as intended (`AppData.secretGamesEnabled = false` in release builds) to prevent trademark disputes with The Tetris Company.
- **Classic Games**: Colossal Cave Adventure, Eliza, Castle, Wander, Dinkum, Chimaera, etc., are covered under open-source licenses (BSD, GNU, public domain). The existing in-app "About" and license files (`bsdlicense.txt`, `gnulicense.txt`, `funhouselicense.txt`, `islandlicense.txt`) provide the necessary legal attribution.

#### D. Privacy Policy & App Nutrition Labels (Guideline 5.1)
- Apple requires a public **Privacy Policy URL** for all apps.
- FunHouse already includes `privacy_en.md` (and localized versions in `de`, `es`, `fr`, `it`, `pt`). Host this markdown or HTML file on a public website (e.g. GitHub Pages or Gepetto domain).
- In App Store Connect App Privacy: Declare "Data Not Collected" (unless you wire AdMob/Analytics in a later phase).

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
    <string>_funhouse._tcp</string>
</array>
```

#### F. App Store Assets
- **App Icon**: 1024x1024 PNG (no transparency, square corners — iOS applies corner rounding automatically).
- **Screenshots**:
  - 6.7-inch iPhone (iPhone 16/17 Pro Max) — 1290 x 2796 px or 1320 x 2868 px.
  - 12.9-inch iPad Pro (optional, but highly recommended since FunHouse layout is adaptive) — 2048 x 2732 px.
  - Can be captured directly from Xcode Simulator (`Cmd + S`).

#### G. Google AdMob & App Store Privacy / ATT Compliance (Mandatory for iOS Monetization)
When you enable Google Mobile Ads (AdMob) on iOS, Apple enforces strict privacy requirements:

1. **App Tracking Transparency (ATT) — Guideline 5.1.2**:
   - If Google Mobile Ads serves personalized ads using the device's IDFA (Identifier for Advertisers), Apple requires showing the native ATT permission dialog before requesting ads.
   - If the user denies permission, Google AdMob automatically falls back to serving non-personalized (contextual) ads without breaking.
   - **`Info.plist` Key**: Must specify `NSUserTrackingUsageDescription`:
     ```xml
     <key>NSUserTrackingUsageDescription</key>
     <string>FunHouse displays ads to keep the games free. Tracking helps us show more relevant ads.</string>
     ```
   - **Triggering ATT in Swift**:
     ```swift
     import AppTrackingTransparency
     import GoogleMobileAds

     func requestTrackingAndInitAds() {
         if #available(iOS 14, *) {
             ATTrackingManager.requestTrackingAuthorization { status in
                 GADMobileAds.sharedInstance().start(completionHandler: nil)
             }
         } else {
             GADMobileAds.sharedInstance().start(completionHandler: nil)
         }
     }
     ```

2. **`GADApplicationIdentifier` in `Info.plist` (CRITICAL CRASH RISK)**:
   - Google AdMob on iOS **will crash on application launch** with an `NSInvalidArgumentException` if your AdMob iOS App ID is not declared in `Info.plist`.
   - You must obtain an iOS App ID in your Google AdMob Console (separate from your Android App ID) and add:
     ```xml
     <key>GADApplicationIdentifier</key>
     <string>ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX</string>
     ```

3. **SKAdNetwork Identifiers (`SKAdNetworkItems`)**:
   - Apple uses the SKAdNetwork framework to track ad clicks and app installs without revealing user identities.
   - Google AdMob requires ~50 third-party partner network identifiers in `Info.plist`. Google provides an official updated list at: [Google AdMob iOS SKAdNetwork guide](https://developers.google.com/admob/ios/ios14#skadnetwork).

4. **App Store Connect Privacy "Nutrition Labels"**:
   When AdMob is active, in the App Store Connect **App Privacy** questionnaire, you must declare:
   - **Data Used to Track You**: Identifiers (Device ID, Advertising ID).
   - **Data Linked to You**: Identifiers, Usage Data (Product Interaction, Advertising Data).
   - **Data Not Linked to You**: Diagnostics (Crash Data, Performance Data).
   - *Note*: If ads are stubbed/disabled in the initial release, you declare **"Data Not Collected"**, making the privacy review instantaneous.

---

## 5. Architectural Dependency Graph & Strategy

```
+-------------------------------------------------------------------------+
|                              ~/valdetaro                                |
+-------------------------------------------------------------------------+
                                     |
         +---------------------------+---------------------------+
         |                                                       |
         v                                                       v
 [gepetto-utils Project]                                 [FunHouse Project]
 - gclog         (add iOS targets)                       - shared:common      (add iOS targets)
 - circum        (add iOS targets)                       - 20 feature modules (add iOS targets)
 - ads-lib       (add iOS targets)                       - composeApp         (add iOS framework)
 - gepetto-utils (add iOS targets)                               |
         |                                                       v
         +------- publishToMavenLocal --------------------> [iosApp (Xcode)]
```

### Execution Strategy
1. **Phase 0 (Foundation)**: Add iOS targets to shared libraries in `gepetto-utils` and publish to `mavenLocal()`.
2. **Phase 1 (Core Models & Shims)**: Add iOS targets to `:shared:common` and implement native iOS actuals (TTS, files, time, sound).
3. **Phase 2 (Game Features)**: Add iOS targets to all 20 `:feature:*` modules and provide iOS actuals for `funhouse-engine-kotlin`.
4. **Phase 3 (App Shell)**: Add iOS framework target to `:composeApp` and create the `iosApp` Xcode project.
5. **Phase 4 (Asset Pipeline)**: Verify asset file installation and runtime persistence on iOS.
6. **Phase 5 (Verification & Testing)**: Build, boot simulator, launch app, and verify gameplay.
7. **Phase 6 (Distribution Preparation)**: Prepare App Store metadata, screenshots, and archive.

---

## 6. Detailed Technical Specification by Module

### 6.1 Step 0: Shared Libraries (`gepetto-utils`) KMP iOS Support
Path: `/Users/luizvaldetaro/valdetaro/gepetto-utils`

Before FunHouse can build for iOS, the four libraries it consumes must support iOS.

#### 6.1.1 `gclog` module
Update `gclog/build.gradle.kts`:
```kotlin
kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    // ...
}
```
Create `src/iosMain/kotlin/club/gepetto/gclog/PlatformIos.kt`:
```kotlin
package club.gepetto.gclog

import platform.Foundation.NSLog

internal actual fun formatString(pattern: String, args: Array<out Any?>): String =
    args.fold(pattern) { acc, arg -> acc.replaceFirst("%s", arg.toString()) }

internal actual fun getStackTag(): String? = null

internal actual fun platformLog(priority: Int, tag: String?, message: String, t: Throwable?) {
    val prefix = when (priority) {
        2 -> "VERBOSE"; 3 -> "DEBUG"; 4 -> "INFO"; 5 -> "WARN"; 6 -> "ERROR" else -> "LOG"
    }
    NSLog("[$prefix]${if (tag != null) "[$tag]" else ""} $message")
    t?.let { NSLog("  Exception: ${it.message}") }
}
```

#### 6.1.2 `circum` module
Update `circum/build.gradle.kts` with iOS targets.
In `src/iosMain/kotlin/club/gepetto/circum/CircumIos.kt`:
```kotlin
package club.gepetto.circum

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import org.koin.core.context.GlobalContext

actual fun circumCurrentTimeMillis(): Long =
    (NSDate().timeIntervalSince1970 * 1000.0).toLong()

actual inline fun <reified CIP : CircumViewModel> circumIntentProcessor(): CIP {
    return GlobalContext.get().get<CIP>()
}
```

#### 6.1.3 `ads-lib` module & Google Mobile Ads (AdMob) on iOS

> [!NOTE]
> **Does iOS support Google's Ad Library?**
> **Yes, absolutely!** Google AdMob fully supports iOS via the official native Apple SDK (`Google-Mobile-Ads-SDK` distributed via Swift Package Manager, CocoaPods, or XCFramework).
> 
> However, Google **does not provide a single unified Kotlin Multiplatform (KMP) artifact**.
> - On Android: `com.google.android.gms:play-services-ads` is an Android JVM AAR library.
> - On iOS: Google's SDK is native Objective-C/Swift (`GoogleMobileAds.framework`).
> 
> Therefore, in Kotlin Multiplatform, we adopt a clean **two-step strategy**:

##### Step A: Phase 0 Stubs (Immediate Game & UI Development)
Provide no-op stubs in `src/iosMain/kotlin/club/gepetto/gcadslib/` (identical to Desktop and Wasm in `ActualsUi.desktop.kt` and `ActualsUi.wasm.kt`). This allows all 20 games, navigation, and simulator testing to be validated without ad popups or missing ad credentials.

1. `ads-lib/src/iosMain/kotlin/club/gepetto/gcadslib/Actuals.ios.kt`:
```kotlin
package club.gepetto.gcadslib

import club.gepetto.GcLog

actual class Bundle actual constructor() {
    private val map = mutableMapOf<String, Any>()
    actual fun putInt(key: String?, value: Int) { key?.let { map[it] = value } }
    actual fun putString(key: String?, value: String?) { if (key != null && value != null) map[key] = value }
    actual fun putLong(key: String?, value: Long) { key?.let { map[it] = value } }
    actual fun putBoolean(key: String?, value: Boolean) { key?.let { map[it] = value } }
}

actual fun initMobileAds(context: Any?) {}
actual fun initAnalytics(context: Any?, tag: String?) {}
actual fun initAnalyticsAndAds(context: Any?, tag: String?) {}
actual fun checkFirstRun(context: Any?): Boolean = false

actual object AnalyticsTracker {
    actual fun logEvent(name: String, params: Bundle?) {
        GcLog.d("Analytics event: $name")
    }
}
```

2. `ads-lib/src/iosMain/kotlin/club/gepetto/gcadslib/ui/ActualsUi.ios.kt`:
```kotlin
package club.gepetto.gcadslib.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual abstract class NativeAd

@Composable
actual fun NativeAdViewComposeBanner(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) {
    Box(modifier)
}

@Composable
actual fun NativeAdViewComposeLargeBanner(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) {
    Box(modifier)
}

@Composable
actual fun NativeAdViewComposeQuarterScreen(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) {
    Box(modifier)
}

@Composable
actual fun NativeAdViewComposeHalfScreen(nativeAd: NativeAd, modifier: Modifier, rightPane: Boolean, title: String, darkMode: Boolean?, refreshTimer: Int) {
    Box(modifier)
}

@Composable
actual fun NativeAdViewComposeEightScreen(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) {
    Box(modifier)
}

@Composable
actual fun AdNativeBanner(modifier: Modifier, testMode: Boolean, refreshTimer: Int) {
    Box(modifier)
}

@Composable
actual fun AdNativeLargeBanner(modifier: Modifier, testMode: Boolean, refreshTimer: Int) {
    Box(modifier)
}

@Composable
actual fun AdNativeQuarterScreen(modifier: Modifier, testMode: Boolean, refreshTimer: Int) {
    Box(modifier)
}

@Composable
actual fun AdNativeHalfScreen(modifier: Modifier, rightPane: Boolean, testMode: Boolean, refreshTimer: Int) {
    Box(modifier)
}

@Composable
actual fun AdNativeEightScreen(modifier: Modifier, testMode: Boolean, refreshTimer: Int) {
    Box(modifier)
}

actual object AdInterstitial {
    actual fun load(context: Any?) {}
    actual fun show(context: Any?) {}
}
```

##### Step B: Phase 5 Production AdMob Integration
When ready to monetize for App Store release:

**Option 1: Swift Package Manager + Compose `UIKitView` Bridge (Cleanest)**:
1. In `iosApp.xcodeproj`, add the Swift Package dependency:
   - Repository URL: `https://github.com/googleads/swift-package-manager-google-mobile-ads.git`
   - Target: `GoogleMobileAds`
2. Create a native banner view in Swift (`iosApp/AdBannerView.swift`):
   ```swift
   import SwiftUI
   import GoogleMobileAds

   struct AdBannerView: UIViewControllerRepresentable {
       let adUnitID: String

       func makeUIViewController(context: Context) -> UIViewController {
           let viewController = UIViewController()
           let banner = GADBannerView(adSize: GADAdSizeBanner)
           banner.adUnitID = adUnitID
           banner.rootViewController = viewController
           banner.load(GADRequest())
           viewController.view.addSubview(banner)
           return viewController
       }

       func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
   }
   ```
3. Expose to Compose Multiplatform via `UIKitView`:
   ```kotlin
   // In iosMain:
   @Composable
   fun IosAdMobBanner(adUnitId: String, modifier: Modifier = Modifier) {
       androidx.compose.ui.interop.UIKitView(
           factory = {
               val banner = platform.UIKit.UIView()
               // Binds to GoogleMobileAds iOS banner
               banner
           },
           modifier = modifier
       )
   }
   ```

**Option 2: KMP Community Wrapper (`admob-kmp`)**:
- Add `io.github.mirzemehdi:admob-kmp` (or `com.github.alorma:admob-kmp`) to `libs.versions.toml`.
- Enables unified `@Composable AdMobBanner(adUnitId = ...)` in `commonMain` while automatically using Google Play Services on Android and Google Mobile Ads on iOS.

**Official Google Test Ad Unit IDs for Testing on iOS**:
- Banner Test ID: `ca-app-pub-3940256099942544/2934735716`
- Interstitial Test ID: `ca-app-pub-3940256099942544/4411468910`
- Rewarded Video Test ID: `ca-app-pub-3940256099942544/1712485313`

#### 6.1.4 `gepetto-utils` module
Update `gepetto-utils/build.gradle.kts` with iOS targets.
In `src/iosMain/kotlin/club/gepetto/`:
- `Platform.kt`: `actual val isAndroidPlatform: Boolean = false`
- `GcAppInfo`: iOS actual object with `versionName`, `versionCode`, `filesDir` pointing to `NSDocumentDirectory`.
- Build & publish locally:
  ```bash
  cd /Users/luizvaldetaro/valdetaro/gepetto-utils
  ./gradlew publishToMavenLocal
  ```

---

### 6.2 Step 1: FunHouse `:shared:common` Module
Path: `/Users/luizvaldetaro/valdetaro/FunHouse/shared/common`

#### 6.2.1 `build.gradle.kts` Updates
Add iOS targets to `kotlin { ... }`:
```kotlin
listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64()
).forEach { iosTarget ->
    iosTarget.binaries.framework {
        baseName = "SharedCommon"
    }
}
```

#### 6.2.2 Implement `src/iosMain/kotlin/com/funhouse/shared/common/utils/PlatformHelpersIos.kt`
```kotlin
package com.funhouse.shared.common.utils

import com.funhouse.shared.common.AppData
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.Foundation.*
import org.jetbrains.skia.Image
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.ImageBitmap

private val speechSynthesizer = AVSpeechSynthesizer()

actual fun speakText(text: String) {
    if (text.isBlank()) return
    val utterance = AVSpeechUtterance(string = text)
    speechSynthesizer.speakUtterance(utterance)
}

actual fun readAssetFile(fileName: String): String? {
    val path = AppData.gameFolderFile as? String ?: return null
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

actual fun installFile(name: String, overwrite: Boolean) {
    val fileManager = NSFileManager.defaultManager
    val destFolder = AppData.gameFolderFile as? String ?: return
    val destPath = "$destFolder/$name"
    if (fileManager.fileExistsAtPath(destPath) && !overwrite) return
    
    // Look up in main bundle resources
    val bundlePath = NSBundle.mainBundle.pathForResource(name, ofType = null)
        ?: NSBundle.mainBundle.pathForResource(name, ofType = null, inDirectory = "compose-resources/files")
    if (bundlePath != null) {
        if (fileManager.fileExistsAtPath(destPath)) {
            fileManager.removeItemAtPath(destPath, error = null)
        }
        fileManager.copyItemAtPath(bundlePath, toPath = destPath, error = null)
    }
}

actual fun loadImageBitmapFromFile(fileName: String): ImageBitmap? {
    val folder = AppData.gameFolderFile as? String ?: return null
    val fullPath = "$folder/$fileName"
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

actual fun stopGameThread() {
    // Coroutines handle lifecycle cleanly on iOS
}

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

#### 6.2.3 Implement Other iOS Actuals in `src/iosMain/kotlin/`:
- **`TimeHelpersIos.kt`**:
  ```kotlin
  package com.funhouse.shared.common.utils
  import platform.Foundation.NSCalendar
  import platform.Foundation.NSCalendarUnitHour
  import platform.Foundation.NSCalendarUnitMinute
  import platform.Foundation.NSDate

  actual fun getCurrentTime(): Pair<Int, Int> {
      val calendar = NSCalendar.currentCalendar
      val components = calendar.components(NSCalendarUnitHour or NSCalendarUnitMinute, fromDate = NSDate())
      return components.hour.toInt() to components.minute.toInt()
  }
  ```
- **`BackHandlerIos.kt`**:
  ```kotlin
  package com.funhouse.shared.common.utils
  import androidx.compose.runtime.Composable

  @Composable
  actual fun CommonBackHandler(enabled: Boolean, onBack: () -> Unit) {
      // No hardware back button on iOS
  }
  ```
- **`CacheHelper.ios.kt`**:
  ```kotlin
  package com.funhouse.shared.common.utils
  import org.jetbrains.compose.resources.StringResource

  actual fun getCacheMap(): MutableMap<StringResource, String> = mutableMapOf()
  ```
- **`BaseKotlinGameSoundsIos.kt`**:
  Sound effects stubs or `AVAudioPlayer` instances for games.
- **Android Shims in `src/iosMain/kotlin/android/`**:
  Provide matching `android.content.Context`, `Resources`, `SoundPool`, and `LocalContext` shims to allow legacy ported games to compile cleanly on iOS without code modifications.

---

### 6.3 Step 2: FunHouse 20 Feature Game Modules
Path: `/Users/luizvaldetaro/valdetaro/FunHouse/feature/*`

All 20 feature modules require iOS targets added to their `build.gradle.kts`:
```kotlin
kotlin {
    androidTarget { ... }
    jvm("desktop") { ... }
    wasmJs { ... }
    
    // Add iOS targets:
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        commonMain.dependencies { ... }
    }
}
```

19 of the 20 modules have 100% of their logic in `commonMain`:
- `blackjack`
- `castle-kotlin`
- `chess`
- `chimaera-kotlin`
- `classic-arcades`
- `colossal-cave-adventure-kotlin`
- `craps`
- `dinkum-kotlin`
- `eliza-kotlin`
- `hangman-kotlin`
- `mistery-mansion-kotlin`
- `poker`
- `roulette`
- `secret-forest-kotlin`
- `slot-machine`
- `space-wars-kotlin`
- `tetric`
- `wander-engine-kotlin`
- `wizards-castle-kotlin`

Once the iOS targets are added, these 19 modules will compile immediately for iOS.

---

### 6.4 Step 3: FunHouse Engine Networking & Concurrency (`feature:funhouse-engine-kotlin`)
Path: `/Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine-kotlin`

This is the only feature module with platform-specific source sets. Create `src/iosMain/kotlin/`:

#### 6.4.1 `ConcurrencyHelpersIos.kt`
```kotlin
package com.funhouse.feature.funhouseenginekotlin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

actual class GcConcurrentMap<K, V> actual constructor() {
    private val map = mutableMapOf<K, V>()
    actual fun put(key: K, value: V): V? = map.put(key, value)
    actual fun remove(key: K): V? = map.remove(key)
    actual operator fun get(key: K): V? = map[key]
    actual operator fun set(key: K, value: V) { map[key] = value }
    actual fun clear() = map.clear()
    actual fun containsKey(key: K): Boolean = map.containsKey(key)
    actual val values: Collection<V> get() = map.values
    actual val entries: Set<Map.Entry<K, V>> get() = map.entries
    actual fun forEach(action: (Map.Entry<K, V>) -> Unit) = map.entries.forEach(action)
    actual fun getOrPut(key: K, defaultValue: () -> V): V = map.getOrPut(key, defaultValue)
    actual operator fun iterator(): Iterator<Map.Entry<K, V>> = map.iterator()
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

actual fun <R> gcSynchronized(lock: Any, block: () -> R): R = block()

actual fun getLocalIps(): List<String> = emptyList()
actual fun isHostPortAvailable(ip: String, port: Int): Boolean = false
actual fun scanSubnetForHost(myIp: String, networkPort: Int, onHostDiscovered: (String) -> Unit) {}
actual fun gcSleep(ms: Long) { platform.posix.usleep(ms.toUInt() * 1000u) }
actual fun isWebPlatform(): Boolean = false
```

#### 6.4.2 `DiscoveryHelperIos.kt` & `GameSocketIos.kt`
Match the `desktopMain` stubs initially to allow single-player games (*Island*, *FunHouse*, *Space Station Aegis*) to run immediately. Network multiplayer can be expanded via Apple `Network.framework` / `NSURLSessionWebSocketTask` in an optional follow-up phase.

---

### 6.5 Step 4: `:composeApp` Module & `iosApp` Xcode Wrapper
Path: `/Users/luizvaldetaro/valdetaro/FunHouse/composeApp`

#### 6.5.1 Update `composeApp/build.gradle.kts`
Add iOS targets with static framework configuration:
```kotlin
listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64()
).forEach { iosTarget ->
    iosTarget.binaries.framework {
        baseName = "ComposeApp"
        isStatic = true
        export(project(":shared:common"))
    }
}
```

#### 6.5.2 Create `composeApp/src/iosMain/kotlin/Main.kt`
```kotlin
package com.gepetto.funhouse

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import platform.Foundation.*
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.Constants
import com.gepetto.gamescollection.CommonConfig
import com.gepetto.funhouse.models.installAssetFiles
import com.gepetto.funhouse.ui.main.MainView
import club.gepetto.utils.GcAppInfo
import club.gepetto.GcLog
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.gepetto.funhouse.intentprocessors.FunHouseIntentProcessor

fun MainViewController(): UIViewController {
    startKoin {
        modules(module {
            single { FunHouseIntentProcessor() }
        })
    }

    val fileManager = NSFileManager.defaultManager
    val docUrl = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).first() as NSURL
    val basePath = docUrl.path ?: ""
    val gamePath = "$basePath/${Constants.GAMES_FOLDER}"
    fileManager.createDirectoryAtPath(gamePath, withIntermediateDirectories = true, attributes = null, error = null)

    AppData.appPackage = "com.gepetto.gamescollection"
    AppData.appName = "FunHouse"
    AppData.packageFolder = basePath
    AppData.packageFolderFile = basePath
    AppData.gameFolder = Constants.GAMES_FOLDER
    AppData.gameFolderFile = gamePath

    GcAppInfo.versionName = CommonConfig.versionName
    GcAppInfo.versionCode = 163L
    GcAppInfo.releaseVersion = true

    AppData.version = "${CommonConfig.versionName}/ios"
    AppData.versionCode = 163L
    AppData.releaseVersion = true
    AppData.secretGamesEnabled = false

    GcLog.plant(GcLog.DebugTree())
    installAssetFiles()

    return ComposeUIViewController {
        MainView()
    }
}
```

#### 6.5.3 Create `iosApp/` Directory & Xcode Project
Create `/Users/luizvaldetaro/valdetaro/FunHouse/iosApp/`:
1. `iosApp/iOSApp.swift`:
   ```swift
   import SwiftUI
   import ComposeApp

   @main
   struct iOSApp: App {
       var body: some Scene {
           WindowGroup {
               ContentView()
                   .ignoresSafeArea(.all)
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
2. `iosApp/Info.plist`:
   - Set bundle identifier: `com.gepetto.gamescollection`
   - Set display name: `FunHouse`
   - Add `ITSAppUsesNonExemptEncryption = false`
   - Supported orientations: Portrait, LandscapeLeft, LandscapeRight (for iPhone & iPad)
3. `iosApp.xcodeproj`:
   - Standard Xcode project with target `iosApp`.
   - Build phase: Run Script invoking:
     ```bash
     cd "$SRCROOT/.."
     ./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
     ```

---

### 6.6 Step 5: Resource & File Installation Pipeline

FunHouse relies on game data files (CSV, JSON, Markdown, text files) located in `composeResources/files/`:
- In Desktop/Android, `installAssetFiles()` copies them to `AppData.gameFolderFile`.
- On iOS, `PlatformHelpersIos.installFile()` copies them from `NSBundle.mainBundle` (or Compose Resources) into the iOS sandbox `Documents/funhouse/` directory.
- This ensures all engines (*Island*, *Eliza*, *Adventure*, *Wander*, *Castle*, etc.) find their game world files at runtime without crashes.

---

## 7. Phase-by-Phase Execution Plan for Agents

Any agent picking up this plan can execute the phases sequentially using these exact steps:

### Phase 0: Shared Libraries (`gepetto-utils`)
- [ ] 0.1: Add iOS targets (`iosX64`, `iosArm64`, `iosSimulatorArm64`) to `gepetto-utils/gclog/build.gradle.kts`.
- [ ] 0.2: Implement `src/iosMain/kotlin/club/gepetto/gclog/PlatformIos.kt`.
- [ ] 0.3: Add iOS targets and implement `src/iosMain/kotlin/club/gepetto/circum/CircumIos.kt` in `circum/build.gradle.kts`.
- [ ] 0.4: Add iOS targets and stub expect declarations in `ads-lib`.
- [ ] 0.5: Add iOS targets and implement `GcAppInfo` and `Platform.kt` in `gepetto-utils`.
- [ ] 0.6: Run `./gradlew publishToMavenLocal` inside `/Users/luizvaldetaro/valdetaro/gepetto-utils`.

### Phase 1: FunHouse `:shared:common` Module
- [ ] 1.1: Add iOS targets to `FunHouse/shared/common/build.gradle.kts`.
- [ ] 1.2: Implement `PlatformHelpersIos.kt` (`speakText`, `readAssetFile`, `installFile`, `loadImageBitmapFromFile`).
- [ ] 1.3: Implement `TimeHelpersIos.kt`, `BackHandlerIos.kt`, `CacheHelper.ios.kt`, and `BaseKotlinGameSoundsIos.kt`.
- [ ] 1.4: Add Android shims (`Context`, `Resources`, `SoundPool`, `LocalContext`) in `src/iosMain/kotlin/android/`.
- [ ] 1.5: Verify with `./gradlew :shared:common:compileKotlinIosSimulatorArm64`.

### Phase 2: Feature Modules (20 Games)
- [ ] 2.1: Add iOS targets to all 19 pure-Kotlin feature `build.gradle.kts` files.
- [ ] 2.2: Add iOS targets to `feature/funhouse-engine-kotlin/build.gradle.kts`.
- [ ] 2.3: Implement `ConcurrencyHelpersIos.kt`, `DiscoveryHelperIos.kt`, and `GameSocketIos.kt` in `feature/funhouse-engine-kotlin`.
- [ ] 2.4: Verify with `./gradlew compileKotlinIosSimulatorArm64`.

### Phase 3: `:composeApp` & `iosApp` Xcode Wrapper
- [ ] 3.1: Add iOS framework target to `FunHouse/composeApp/build.gradle.kts`.
- [ ] 3.2: Implement `composeApp/src/iosMain/kotlin/Main.kt` (`MainViewController`).
- [ ] 3.3: Generate/create `iosApp/iosApp.xcodeproj`, `iOSApp.swift`, `Info.plist`, and `Assets.xcassets`.
- [ ] 3.4: Verify framework build with `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`.

### Phase 4: Simulator Launch & Gameplay Verification
- [ ] 4.1: Boot simulator: `xcrun simctl boot "iPhone 17" && open -a Simulator`.
- [ ] 4.2: Build and run `iosApp` via `xcodebuild` targeting booted simulator.
- [ ] 4.3: Verify game category navigation (Skill, Arcade, Chatbots, Chance, Adventure).
- [ ] 4.4: Verify test games:
  - Compose games: Blackjack, Chess, Slot Machine, Classic Arcades.
  - Text games: Eliza, Adventure, FunHouse, Wander, Castle.
- [ ] 4.5: Verify dark mode toggle (`Cmd + Shift + A`) and landscape orientation (`Cmd + Arrow`).

#### Phase 5: Google Mobile Ads (AdMob) iOS Integration (Monetization)
- [ ] 5.1: Add Google Mobile Ads SDK (`Google-Mobile-Ads-SDK`) via Swift Package Manager to `iosApp.xcodeproj` (or via KMP wrapper `admob-kmp`).
- [ ] 5.2: Configure `GADApplicationIdentifier` in `iosApp/Info.plist` with your AdMob iOS App ID.
- [ ] 5.3: Add `NSUserTrackingUsageDescription` (App Tracking Transparency prompt) and `SKAdNetworkItems` to `iosApp/Info.plist`.
- [ ] 5.4: Implement ATT authorization trigger in `iOSApp.swift` (`ATTrackingManager.requestTrackingAuthorization`).
- [ ] 5.5: Wire `AdBannerView` bridge into `iosMain` and test with Google's iOS Test Ad Unit ID (`ca-app-pub-3940256099942544/2934735716`).

### Phase 6: App Store Connect & Distribution Preparation
- [ ] 6.1: Create Apple Developer Account ($99/year individual enrollment).
- [ ] 6.2: Create App record in App Store Connect with bundle ID `com.gepetto.gamescollection`.
- [ ] 6.3: Capture simulator screenshots (`Cmd + S`) for 6.7" iPhone and 12.9" iPad.
- [ ] 6.4: Complete Age Rating questionnaire (declaring Simulated Gambling for Casino games).
- [ ] 6.5: Complete App Store Privacy questionnaire: Declare Tracking/Identifiers/Diagnostics for Google AdMob (or "Data Not Collected" if ads are disabled).
- [ ] 6.6: Publish Privacy Policy URL.
- [ ] 6.7: Create release archive via Xcode and submit to TestFlight.

---

## 8. Living Document Changelog

| Date | Author / Agent | Summary of Changes |
|---|---|---|
| 2026-09-17 | Initial Agent | Rev 1: Created living document `IOS_PORT_PLAN.md`. Detailed tools, testing without physical device, Apple Developer setup, App Store approval guidelines (simulated gambling), module technical specs, and 6-phase execution plan. |
| 2026-09-17 | Initial Agent | Rev 2: Clarified Google Mobile Ads (AdMob) iOS SDK support, explaining native Apple SDK vs Android AAR, phased integration, ATT prompt, and Info.plist requirements. |
| 2026-09-17 | Initial Agent | Rev 3: Full Google AdMob iOS integration added into the plan: complete code samples (stubs vs SPM/UIKitView bridge), ATT authorization Swift code, GADApplicationIdentifier crash prevention, SKAdNetworkItems, App Store Privacy Nutrition Labels, and dedicated Phase 5 execution checklist. |

---

## 9. Bug, Blocker & Issue Tracker

| ID | Module / Component | Description | Status | Workaround / Resolution |
|---|---|---|---|---|
| BUG-001 | `gepetto-utils` | iOS targets not declared in `gepetto-utils` library | Open (Phase 0) | Implement iOS targets and publish to `mavenLocal()`. |
| BUG-002 | `funhouse-engine-kotlin` | Platform-specific WebSocket & Concurrency code on JVM/Android | Open (Phase 2) | Implement Coroutine Channel queue, usleep, and stubs for discovery/sockets. |
| BUG-003 | App Store Review | Risk of rejection due to undeclared casino games | Documented (Phase 6) | Declare "Simulated Gambling" in Age Rating questionnaire (12+/17+ rating). |
| BUG-004 | App Store Review | Risk of rejection if "Tetric" infringes Tetris trademark | Documented (Phase 6) | Ensure `AppData.secretGamesEnabled = false` for release builds. |
| BUG-005 | Google AdMob iOS | Crash on launch if GADApplicationIdentifier is missing | Documented (Phase 5) | Ensure valid GADApplicationIdentifier key is present in Info.plist. |

*(Agents executing this plan: Add new entries above whenever a bug or obstacle is encountered during implementation)*
