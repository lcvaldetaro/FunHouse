# iOS Port Plan: FunHouse Game Collection (Living Document)

> **Document Status**: Living Roadmap & Technical Specification  
> **Target Application**: FunHouse Game Collection (`/Users/luizvaldetaro/valdetaro/FunHouse`)  
> **Workspace**: `/Users/luizvaldetaro/valdetaro`  
> **Document Location**: `.agents/IOS_PORT_PLAN.md`  
> **Last Updated**: 2026-09-18 (Rev 4 — Codebase synchronization with JDK 21 / Kotlin 2.4.20 / Compose 1.12.0, exact expect/actual signatures for gepetto-utils suite, missing java.io/Thread shims for iOS, crash fixes for Koin and AppData.gameFolderFile, automated Xcode project generation)  
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

The app depends on four shared KMP libraries in `~/valdetaro/gepetto-utils`:
- `club.gepetto:gepetto-utils:2.1.1`
- `club.gepetto:circum:2.1.1`
- `club.gepetto:gclog:0.1.1`
- `club.gepetto:gcadslib:0.4.1`

### Current Toolchain & Dependencies (Updated 2026-09-18)
- **Java/JDK**: OpenJDK 21 (`JavaVersion.VERSION_21`, `JVM_21`)
- **Gradle Wrapper**: 9.7.1
- **Kotlin**: 2.4.20
- **Android Gradle Plugin (AGP)**: 9.4.0
- **JetBrains Compose Multiplatform**: 1.12.0
- **Navigation 3**: `androidx.navigation3:1.1.7`, `androidx.compose.material3.adaptive:1.3.0`

### Current Porting Obstacle
Currently, neither `gepetto-utils` nor `FunHouse` declares Apple iOS targets (`iosArm64`, `iosSimulatorArm64`, `iosX64`). To run on iOS:
1. Shared libraries must build for iOS targets and publish to `mavenLocal()`.
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
  - Verification confirms Xcode is installed at `/Applications/Xcode.app` (Xcode 27.0) and active developer directory points to `/Applications/Xcode.app/Contents/Developer`.
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
1. **Compile iOS Simulator Framework via Gradle**:
   ```bash
   cd /Users/luizvaldetaro/valdetaro/FunHouse
   ./gradlew :composeApp:compileKotlinIosSimulatorArm64
   ```
2. **Build and Run via Terminal (`xcodebuild` + `simctl`)**:
   ```bash
   # Build the Xcode scheme targeting the booted simulator
   xcodebuild -project iosApp/iosApp.xcodeproj \
              -scheme iosApp \
              -destination 'platform=iOS Simulator,name=iPhone 17' \
              -configuration Debug \
              build

   # Install the built .app onto the booted simulator
   xcrun simctl install booted iosApp/build/Release-iphonesimulator/iosApp.app

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

#### A. Guideline 4.7: Mini-Apps & Game Collections
- FunHouse contains 20 classic games in one binary. Apple explicitly allows game collections and retro game engines under Guideline 4.7, provided all software inside complies with privacy and content rules, and does not require third-party app stores.
- Every game in the collection must be fully functional and stable.

#### B. The "Simulated Gambling" Age Rating Gotcha (CRITICAL)
- FunHouse includes **Blackjack**, **Craps**, **Roulette**, **Slot Machine**, and **Poker**.
- In the App Store Connect Age Rating questionnaire, you **MUST** declare:
  - **Simulated Gambling**: Answer **"Frequent / Intense"** (or "Infrequent/Mild").
  - Apple will automatically assign a **12+** or **17+** age rating.
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
    <string>_funhouse._tcp</string>
</array>
```

#### F. App Store Assets
- **App Icon**: 1024x1024 PNG (no transparency, square corners — iOS applies corner rounding automatically).
- **Screenshots**:
  - 6.7-inch iPhone (iPhone 16/17 Pro Max) — 1290 x 2796 px or 1320 x 2868 px.
  - 12.9-inch iPad Pro — 2048 x 2732 px.
  - Can be captured directly from Xcode Simulator (`Cmd + S`).

#### G. Google AdMob & App Store Privacy / ATT Compliance (Mandatory for iOS Monetization)
1. **App Tracking Transparency (ATT) — Guideline 5.1.2**:
   - `Info.plist` key: `NSUserTrackingUsageDescription`.
   - Native ATT authorization dialog must be presented before requesting personalized ads.
2. **`GADApplicationIdentifier` in `Info.plist` (CRITICAL CRASH RISK)**:
   - Google AdMob on iOS **will crash on launch** with `NSInvalidArgumentException` if `GADApplicationIdentifier` is missing.
3. **SKAdNetwork Identifiers**: Must be added to `Info.plist`.

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
 [gepetto-utils Project]                                             [FunHouse Project]
  Step 0.1: circum         (leaf dependency)                         Step 1: shared:common
  Step 0.2: gepetto-utils  (depends on circum)                               (iOS actuals + runtime shims)
  Step 0.3: gclog          (depends on gepetto-utils)                        |
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
1. **Phase 0 (Foundation)**: Add iOS targets to shared libraries in `gepetto-utils` in correct topological dependency order (`circum` -> `gepetto-utils` -> `gclog` -> `ads-lib`) and publish to `mavenLocal()`.
2. **Phase 1 (Core Models & Shims)**: Add iOS targets to `:shared:common` and implement native iOS actuals (TTS, files, time, sound) PLUS Java/Android runtime shims (`java.io.File`, `BufferedReader`, `Thread.sleep`, `Random`).
3. **Phase 2 (Game Features)**: Add iOS targets to all 20 `:feature:*` modules and provide iOS actuals for `funhouse-engine-kotlin`.
4. **Phase 3 (App Shell)**: Add iOS static framework target to `:composeApp` and generate the `iosApp` Xcode project.
5. **Phase 4 (Asset Pipeline & Simulator Testing)**: Verify asset file installation and test on iOS simulator.
6. **Phase 5 (Monetization)**: Wire AdMob via Swift Package Manager and ATT dialog (when ready).
7. **Phase 6 (Distribution)**: Prepare App Store metadata, screenshots, and archive.

---

## 6. Detailed Technical Specification by Module

### 6.1 Step 0: Shared Libraries (`gepetto-utils`) KMP iOS Support
Path: `/Users/luizvaldetaro/valdetaro/gepetto-utils`

> [!IMPORTANT]
> The four libraries must be built in this strict dependency order:
> 1. `:circum`
> 2. `:gepetto-utils`
> 3. `:gclog`
> 4. `:ads-lib`

#### 6.1.1 `circum` Module
Update `circum/build.gradle.kts`:
```kotlin
kotlin {
    // Android, JVM desktop, WasmJs...
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}
```
Create `circum/src/iosMain/kotlin/club/gepetto/circum/CircumIos.kt`:
```kotlin
package club.gepetto.circum

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun circumCurrentTimeMillis(): Long =
    (NSDate().timeIntervalSince1970 * 1000.0).toLong()

@Composable
actual inline fun <reified CIP : CircumViewModel> circumIntentProcessor(
    initialState: Any?,
    initialCommand: Any?,
): CIP {
    val cm = koinInject<CIP>()
    if (initialCommand != null) (cm as CircumIntentProcessor<Any, Any, Any>).sendIntentCommand(initialCommand)
    if (initialState != null) (cm as CircumIntentProcessor<Any, Any, Any>).setState(initialState)
    return cm
}
```

#### 6.1.2 `gepetto-utils` Module
Update `gepetto-utils/build.gradle.kts`:
```kotlin
kotlin {
    // Android, JVM desktop, WasmJs...
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val iosMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}
```
Add `ktor-client-darwin` to `gradle/libs.versions.toml`:
```toml
[libraries]
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktorClientCore" }
```

Implement `gepetto-utils/src/iosMain/kotlin/`:
1. `club/gepetto/composeutils/PlatformFile.ios.kt`:
```kotlin
package club.gepetto.composeutils

import platform.Foundation.*

actual class PlatformFile {
    val path: String

    actual constructor(pathname: String) { this.path = pathname }
    actual constructor(parent: String, child: String) { this.path = "$parent/$child" }
    actual constructor(parent: PlatformFile?, child: String) {
        this.path = if (parent != null) "${parent.path}/$child" else child
    }

    actual val parentFile: PlatformFile?
        get() {
            val idx = path.lastIndexOf('/')
            return if (idx > 0) PlatformFile(path.substring(0, idx)) else null
        }
    actual val absolutePath: String get() = path

    actual fun exists(): Boolean = NSFileManager.defaultManager.fileExistsAtPath(path)

    actual fun writeText(text: String) {
        val nsStr = NSString.create(string = text)
        nsStr.writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
    }

    actual fun readText(): String {
        val data = NSData.dataWithContentsOfFile(path) ?: return ""
        return NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString() ?: ""
    }

    actual fun writeBytes(bytes: ByteArray) {
        val nsStr = bytes.decodeToString()
        writeText(nsStr)
    }

    actual fun mkdir(): Boolean =
        NSFileManager.defaultManager.createDirectoryAtPath(path, withIntermediateDirectories = false, attributes = null, error = null)

    actual fun mkdirs(): Boolean =
        NSFileManager.defaultManager.createDirectoryAtPath(path, withIntermediateDirectories = true, attributes = null, error = null)

    actual fun length(): Long {
        val attrs = NSFileManager.defaultManager.attributesOfItemAtPath(path, error = null) ?: return 0L
        return (attrs[NSFileSize] as? NSNumber)?.longValue ?: 0L
    }

    actual fun lastModified(): Long {
        val attrs = NSFileManager.defaultManager.attributesOfItemAtPath(path, error = null) ?: return 0L
        val date = attrs[NSFileModificationDate] as? NSDate ?: return 0L
        return (date.timeIntervalSince1970 * 1000.0).toLong()
    }

    actual fun delete(): Boolean = NSFileManager.defaultManager.removeItemAtPath(path, error = null)
}
```

2. `club/gepetto/composeutils/Actuals.ios.kt`:
```kotlin
package club.gepetto.composeutils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual class PlatformBitmap(val imageBitmap: ImageBitmap)
actual fun PlatformBitmap.toImageBitmap(): ImageBitmap = this.imageBitmap

actual abstract class Context

actual fun gcCurrentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()

@Composable
actual fun QrCodeView(data: String, modifier: Modifier) { Box(modifier) }

actual fun createFileImageFromAssets(ctx: Context, image: PlatformFile, resource: String) {}
actual fun createFileImageFromDrawable(ctx: Context, imageFile: PlatformFile, resource: Int) {}
actual fun createBitmapFromDrawable(ctx: Context, resource: Int): PlatformBitmap? = null

@Composable
actual fun CreateFileImageFromDrawable(imageFile: PlatformFile, resource: Int) {}

actual fun invertBitmapColors(bitmap: PlatformBitmap): PlatformBitmap? = null

@Composable
actual fun GcCoil2Image(
    url: String,
    modifier: Modifier,
    contentScale: ContentScale,
    contentDescription: String?,
    errorImage: Int,
    fallbackImage: Int,
    placeHolderImage: Int,
    cache: Boolean,
    onError: (Any) -> Unit,
    onSuccess: () -> Unit
) {
    Box(modifier)
}

actual val isAndroidPlatform: Boolean = false

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {}

actual fun textAsBitmap(text: String, textSize: Float, textColor: Int): PlatformBitmap {
    return PlatformBitmap(androidx.compose.ui.graphics.ImageBitmap(1, 1))
}
```

3. `club/gepetto/composeutils/PlatformHttpClient.ios.kt`:
```kotlin
package club.gepetto.composeutils

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin

actual fun createPlatformHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Darwin) {
        block()
    }
}
```

4. `club/gepetto/composeutils/webpage/WebComposeUtils.ios.kt`:
```kotlin
package club.gepetto.composeutils.webpage

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable actual fun GcHtmlView(htmlData: String, modifier: Modifier) { Box(modifier) { Text(htmlData) } }
@Composable actual fun GcHtmlText(htmlText: String, modifier: Modifier) { Box(modifier) { Text(htmlText) } }
@Composable actual fun GcHtmlFile(htmlFolder: String, htmlFilename: String, modifier: Modifier) { Box(modifier) }
```

5. `club/gepetto/utils/Utils.ios.kt`:
```kotlin
package club.gepetto.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance

actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default

private val synthesizer = AVSpeechSynthesizer()
actual fun gCSpeak(text: String) {
    if (text.isBlank()) return
    synthesizer.speakUtterance(AVSpeechUtterance(string = text))
}

actual fun isRunningOnChromebook(context: Any): Boolean = false

actual object GcAppInfo {
    actual var application_Context: Any? = null
    actual var versionCode: Long? = 1L
    actual var versionName: String? = "1.0"
    actual var ttsHandle: Any? = null
    actual var appPackageFolder: String = ""
    actual var releaseVersion: Boolean = false
}
```

#### 6.1.3 `gclog` Module
Update `gclog/build.gradle.kts` with iOS targets.
Create `gclog/src/iosMain/kotlin/club/gepetto/PlatformIos.kt`:
> [!IMPORTANT]
> The package MUST be `club.gepetto` (matching `GcLog.kt` in `commonMain`), NOT `club.gepetto.gclog`.

```kotlin
package club.gepetto

import platform.Foundation.NSLog

internal actual fun formatString(pattern: String, args: Array<out Any?>): String =
    args.fold(pattern) { acc, arg -> acc.replaceFirst("%s", arg.toString()) }

internal actual fun getStackTag(): String? = null

internal actual fun platformLog(priority: Int, tag: String?, message: String, t: Throwable?) {
    val level = when (priority) {
        2 -> "V"; 3 -> "D"; 4 -> "I"; 5 -> "W"; 6 -> "E" else -> "LOG"
    }
    val logTag = tag ?: "GcLog"
    NSLog("[$level/$logTag] $message")
    t?.let { NSLog("  Exception: ${it.message}") }
}
```

#### 6.1.4 `ads-lib` Module
Update `ads-lib/build.gradle.kts` with iOS targets.

1. `ads-lib/src/iosMain/kotlin/club/gepetto/gcadslib/Actuals.ios.kt`:
```kotlin
package club.gepetto.gcadslib

import club.gepetto.composeutils.Context

actual class Bundle actual constructor() {
    private val map = mutableMapOf<String, Any>()
    actual fun putInt(key: String?, value: Int) { key?.let { map[it] = value } }
    actual fun putString(key: String?, value: String?) { if (key != null && value != null) map[key] = value }
    actual fun putLong(key: String?, value: Long) { key?.let { map[it] = value } }
    actual fun putBoolean(key: String?, value: Boolean) { key?.let { map[it] = value } }
}

actual fun initMobileAds(context: Context) {}
actual fun initAnalytics(context: Context, tag: String?) {}
actual fun initAnalyticsAndAds(context: Context, tag: String?) {}
actual fun checkFirstRun(context: Context): Boolean = false

actual object AnalyticsTracker {
    actual val measurementId: String get() = ""
    actual fun init(context: Context) {}
    actual fun trackAnalyticsToggle(enabled: Boolean, serverVersion: String) {}
    actual fun trackRaceStart(
        numberOfLanes: Int, driverCount: Int, isDemo: Boolean,
        heatRotationType: String, heatScoringMethod: String,
        overallScoringMethod: String, fuelSystem: String,
        hardwareInterface: String, serverVersion: String
    ) {}
    actual fun logEvent(screenView: String, bundle: Bundle) {}
    actual fun logEvent(tag: String, key: String, value: Int) {}
    actual fun logEvent(tag: String) {}
    actual fun logScreenView(screenView: String) {}
    actual fun logNewUser(context: Context, tag: String?) {}
}
```

2. `ads-lib/src/iosMain/kotlin/club/gepetto/gcadslib/ui/ActualsUi.ios.kt`:
```kotlin
package club.gepetto.gcadslib.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import club.gepetto.composeutils.Context

actual abstract class NativeAd

@Composable actual fun NativeAdViewComposeQuarterScreen(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) { Box(modifier) }
@Composable actual fun NativeAdViewComposeEightScreen(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) { Box(modifier) }
@Composable actual fun NativeAdViewComposeHalfScreen(nativeAd: NativeAd, modifier: Modifier, rightPane: Boolean, title: String, darkMode: Boolean?, refreshTimer: Int) { Box(modifier) }
@Composable actual fun NativeAdViewComposeBanner(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) { Box(modifier) }
@Composable actual fun NativeAdViewComposeLargeBanner(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) { Box(modifier) }
@Composable actual fun NativeAdViewComposeFullBanner(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) { Box(modifier) }
@Composable actual fun NativeAdViewComposeLeaderboard(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) { Box(modifier) }
@Composable actual fun NativeAdViewComposeMediumRectangle(nativeAd: NativeAd, modifier: Modifier, title: String, darkMode: Boolean?, refreshTimer: Int) { Box(modifier) }

@Composable actual fun AdNative(modifier: Modifier, adUnit: String, rightPane: Boolean, title: String, darkMode: Boolean?, refreshTimer: Int, adImpressionTag: String, adUnitTag: String, adErrorTag: String, onAdLoaded: () -> Unit, onAdImpression: () -> Unit, onAdClicked: () -> Unit, onError: () -> Unit) { Box(modifier) }
@Composable actual fun AdNativeBanner(modifier: Modifier, adUnit: String, title: String, darkMode: Boolean?, refreshTimer: Int, adImpressionTag: String, adUnitTag: String, adErrorTag: String, onAdLoaded: () -> Unit, onAdImpression: () -> Unit, onAdClicked: () -> Unit, onError: () -> Unit) { Box(modifier) }
@Composable actual fun AdNativeLargeBanner(modifier: Modifier, adUnit: String, title: String, darkMode: Boolean?, refreshTimer: Int, adImpressionTag: String, adUnitTag: String, adErrorTag: String, onAdLoaded: () -> Unit, onAdImpression: () -> Unit, onAdClicked: () -> Unit, onError: () -> Unit) { Box(modifier) }
@Composable actual fun AdNativeFullBanner(modifier: Modifier, adUnit: String, title: String, darkMode: Boolean?, refreshTimer: Int, adImpressionTag: String, adUnitTag: String, adErrorTag: String, onAdLoaded: () -> Unit, onAdImpression: () -> Unit, onAdClicked: () -> Unit, onError: () -> Unit) { Box(modifier) }
@Composable actual fun AdNativeLeaderboard(modifier: Modifier, adUnit: String, title: String, darkMode: Boolean?, refreshTimer: Int, adImpressionTag: String, adUnitTag: String, adErrorTag: String, onAdLoaded: () -> Unit, onAdImpression: () -> Unit, onAdClicked: () -> Unit, onError: () -> Unit) { Box(modifier) }
@Composable actual fun AdNativeMediumRectangle(modifier: Modifier, adUnit: String, title: String, darkMode: Boolean?, refreshTimer: Int, adImpressionTag: String, adUnitTag: String, adErrorTag: String, onAdLoaded: () -> Unit, onAdImpression: () -> Unit, onAdClicked: () -> Unit, onError: () -> Unit) { Box(modifier) }

@Composable actual fun AdBanner(modifier: Modifier, adUnit: String, adSize: AdBannerSize, adImpressionTag: String, adErrorTag: String, adClickTag: String, adSizeTag: String, adWidthDp: Dp?, onAdLoaded: () -> Unit, onAdImpression: () -> Unit, onAdClicked: () -> Unit, onError: () -> Unit) { Box(modifier) }
@Composable actual fun AdBannerAdaptive(adUnitId: String, modifier: Modifier, adWidthDp: Dp?) { Box(modifier) }
@Composable actual fun AdBannerCard(modifier: Modifier, adSize: AdBannerSize) { Box(modifier) }
@Composable actual fun AdBannerBox(modifier: Modifier, adSize: AdBannerSize) { Box(modifier) }

@Composable actual fun GcAd(modifier: Modifier, rightPane: Boolean, adSize: AdBannerSize, title: String, darkMode: Boolean?, refreshTimer: Int, usingNativeAd: Boolean, adImpressionTag: String, adErrorTag: String, adClickTag: String, adSizeTag: String, adUnitTag: String, adUnitId: String, adWidthDp: Dp?, onAdLoaded: () -> Unit, onAdImpression: () -> Unit, onAdClicked: () -> Unit, onError: () -> Unit) { Box(modifier) }

actual object AdInterstitial {
    actual fun load(context: Context) {}
    actual fun show(context: Context, onAdDismissed: () -> Unit) { onAdDismissed() }
    actual fun isReady(): Boolean = false
    actual fun isLoading(): Boolean = false
}

@Composable actual fun InterstitialAd(content: @Composable () -> Unit) { content() }
```

#### 6.1.5 Publishing to `mavenLocal()`
```bash
cd /Users/luizvaldetaro/valdetaro/gepetto-utils
./gradlew publishToMavenLocal
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
    listOf(
        iosX64(),
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
12+ game modules import `java.io.*` and `Thread` in `commonMain`. Provide these shims in `src/iosMain/kotlin/`:

#### A. `java/io/File.kt`:
```kotlin
package java.io

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

    override fun readBytes(): ByteArray = readText().encodeToByteArray()
    override fun bufferedReader(): BufferedReader = BufferedReader(this)
    override fun bufferedWriter(): BufferedWriter = BufferedWriter(this)
    override fun copyTo(target: File, overwrite: Boolean): File {
        if (overwrite || !target.exists()) {
            target.writeText(this.readText())
        }
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

class Random {
    constructor()
    constructor(seed: Long)
    fun nextInt(): Int = KotlinRandom.nextInt()
    fun nextInt(bound: Int): Int = KotlinRandom.nextInt(bound)
    fun nextDouble(): Double = KotlinRandom.nextDouble()
    fun nextFloat(): Float = KotlinRandom.nextFloat()
    fun nextLong(): Long = KotlinRandom.nextLong()
    fun nextBoolean(): Boolean = KotlinRandom.nextBoolean()
}

class Locale {
    companion object { fun getDefault(): Locale = Locale() }
    val language: String get() = "en"
}
```

#### C. `kotlin/SystemAndThread.kt`:
```kotlin
package kotlin

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

class Thread {
    private var interrupted = false
    fun interrupt() { interrupted = true }
    val isInterrupted: Boolean get() = interrupted

    companion object {
        fun sleep(millis: Long) {
            platform.posix.usleep((millis * 1000L).toUInt())
        }
        fun currentThread(): Thread = Thread()
    }
}

class InterruptedException : Exception()

object System {
    fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
    fun exit(status: Int) {}
}

object Math {
    fun abs(x: Int): Int = kotlin.math.abs(x)
    fun min(a: Int, b: Int): Int = kotlin.math.min(a, b)
    fun max(a: Int, b: Int): Int = kotlin.math.max(a, b)
    fun sqrt(x: Double): Double = kotlin.math.sqrt(x)
    fun random(): Double = kotlin.random.Random.nextDouble()
}
```

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

private val speechSynthesizer = AVSpeechSynthesizer()

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

actual fun installFile(name: String, overwrite: Boolean) {
    val fileManager = NSFileManager.defaultManager
    val destFolder = AppData.gameFolderFile as? java.io.File ?: return
    val destPath = "${destFolder.path}/$name"
    if (fileManager.fileExistsAtPath(destPath) && !overwrite) return

    val resPath = NSBundle.mainBundle.resourcePath ?: ""
    val candidatePaths = listOf(
        "$resPath/compose-resources/com.funhouse.shared.common.generated.resources/files/$name",
        "$resPath/compose-resources/files/$name",
        NSBundle.mainBundle.pathForResource(name, ofType = null) ?: "",
        NSBundle.mainBundle.pathForResource(name, ofType = null, inDirectory = "compose-resources/files") ?: ""
    ).filter { it.isNotEmpty() && fileManager.fileExistsAtPath(it) }

    val sourcePath = candidatePaths.firstOrNull() ?: return
    if (fileManager.fileExistsAtPath(destPath)) {
        fileManager.removeItemAtPath(destPath, error = null)
    }
    fileManager.copyItemAtPath(sourcePath, toPath = destPath, error = null)
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

    // Add iOS targets:
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies { ... }
    }
}
```

Because all 19 pure-Kotlin feature modules depend on `:shared:common` and consume its `java.io.*` / `Thread` shims, they will compile cleanly for iOS without any code modifications!

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

actual fun <R> gcSynchronized(lock: Any, block: () -> R): R {
    platform.objc.objc_sync_enter(lock)
    try {
        return block()
    } finally {
        platform.objc.objc_sync_exit(lock)
    }
}

actual fun getLocalIps(): List<String> = emptyList()
actual fun isHostPortAvailable(ip: String, port: Int): Boolean = false
actual fun scanSubnetForHost(myIp: String, networkPort: Int, onHostDiscovered: (String) -> Unit) {}
actual fun gcSleep(ms: Long) { platform.posix.usleep((ms * 1000L).toUInt()) }
actual fun isWebPlatform(): Boolean = false
```

### 6.4.2 `DiscoveryHelperIos.kt` & `GameSocketIos.kt`
Match the `desktopMain` stubs initially to allow single-player games (*Island*, *FunHouse*, *Space Station Aegis*) to run immediately.

---

## 6.5 Step 4: `:composeApp` Module & `iosApp` Xcode Project Wrapper
Path: `/Users/luizvaldetaro/valdetaro/FunHouse/composeApp`

### 6.5.1 Update `composeApp/build.gradle.kts`
```kotlin
kotlin {
    // Android, JVM desktop, WasmJs...
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
}
```

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

    val vCode = 163L
    GcAppInfo.versionName = CommonConfig.versionName
    GcAppInfo.versionCode = vCode
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

### 6.5.3 Automated Xcode Project Generation Script
To avoid manual GUI project setup, run this python generator script:

```python
# File: scripts/create_ios_project.py
import os

PROJECT_DIR = "/Users/luizvaldetaro/valdetaro/FunHouse/iosApp"
os.makedirs(f"{PROJECT_DIR}/iosApp.xcodeproj", exist_ok=True)
os.makedirs(f"{PROJECT_DIR}/iosApp", exist_ok=True)

# 1. Write iOSApp.swift
with open(f"{PROJECT_DIR}/iosApp/iOSApp.swift", "w") as f:
    f.write('''import SwiftUI
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
''')

# 2. Write Info.plist
with open(f"{PROJECT_DIR}/iosApp/Info.plist", "w") as f:
    f.write('''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>$(EXECUTABLE_NAME)</string>
    <key>CFBundleIdentifier</key>
    <string>com.gepetto.gamescollection</string>
    <key>CFBundleName</key>
    <string>FunHouse</string>
    <key>CFBundleShortVersionString</key>
    <string>2.1.63</string>
    <key>CFBundleVersion</key>
    <string>163</string>
    <key>ITSAppUsesNonExemptEncryption</key>
    <false/>
    <key>UISupportedInterfaceOrientations</key>
    <array>
        <string>UIInterfaceOrientationPortrait</string>
        <string>UIInterfaceOrientationLandscapeLeft</string>
        <string>UIInterfaceOrientationLandscapeRight</string>
    </array>
</dict>
</plist>
''')

# 3. Create project.pbxproj with embedAndSignAppleFrameworkForXcode build phase
# (Full template generates native targets linked with ComposeApp.framework)
print("iosApp Xcode project template created successfully.")
```

---

## 6.6 Step 5: Resource & File Installation Pipeline

FunHouse relies on game data files (CSV, JSON, Markdown, text files) located in `composeResources/files/`:
- In Desktop/Android, `installAssetFiles()` copies them to `AppData.gameFolderFile`.
- On iOS, `PlatformHelpersIos.installFile()` copies them from the application bundle's `compose-resources/` into the iOS sandbox `Documents/funhouse/` directory.
- Because `AppData.gameFolderFile` is an instance of `java.io.File(gamePath)`, calls like:
  ```kotlin
  val licenseFile = File(AppData.gameFolderFile as File, game.licenseFile!!.fileName)
  ```
  succeed with zero modifications to common game logic.

---

## 7. Phase-by-Phase Execution Plan for Agents

Any agent picking up this plan can execute the phases sequentially using these exact steps:

### Phase 0: Shared Libraries (`gepetto-utils`)
- [ ] 0.1: Add iOS targets (`iosX64`, `iosArm64`, `iosSimulatorArm64`) and implement `CircumIos.kt` in `gepetto-utils/circum`.
- [ ] 0.2: Add iOS targets, `ktor-client-darwin`, and implement `PlatformFile.ios.kt`, `Actuals.ios.kt`, `PlatformHttpClient.ios.kt`, `WebComposeUtils.ios.kt`, `Utils.ios.kt` in `gepetto-utils/gepetto-utils`.
- [ ] 0.3: Add iOS targets and implement `PlatformIos.kt` (in `package club.gepetto`) in `gepetto-utils/gclog`.
- [ ] 0.4: Add iOS targets and implement full stubs (`Actuals.ios.kt`, `ActualsUi.ios.kt`) matching `Expectations.kt` and `ExpectationsUi.kt` in `gepetto-utils/ads-lib`.
- [ ] 0.5: Run `./gradlew publishToMavenLocal` inside `/Users/luizvaldetaro/valdetaro/gepetto-utils`.

### Phase 1: FunHouse `:shared:common` Module & Shims
- [ ] 1.1: Add iOS targets and `-Xallow-kotlin-package` to `FunHouse/shared/common/build.gradle.kts`.
- [ ] 1.2: Implement `java.io.File`, `BufferedReader`, `BufferedWriter`, `IOException`, `PrintWriter` shims in `src/iosMain/kotlin/java/io/`.
- [ ] 1.3: Implement `java.util.Random` and `Locale` in `src/iosMain/kotlin/java/util/`.
- [ ] 1.4: Implement `Thread.sleep`, `System`, and `Math` in `src/iosMain/kotlin/kotlin/`.
- [ ] 1.5: Implement `android/content/Context`, `MediaMocks`, `SuppressLint`, and `LocalContext` in `src/iosMain/kotlin/android/`.
- [ ] 1.6: Implement `PlatformHelpersIos.kt`, `TimeHelpersIos.kt`, `BackHandlerIos.kt`, `CacheHelper.ios.kt`, and `BaseKotlinGameSoundsIos.kt`.
- [ ] 1.7: Verify compilation: `./gradlew :shared:common:compileKotlinIosSimulatorArm64`.

### Phase 2: Feature Modules (20 Games)
- [ ] 2.1: Add iOS targets to all 19 pure-Kotlin feature `build.gradle.kts` files.
- [ ] 2.2: Add iOS targets to `feature/funhouse-engine-kotlin/build.gradle.kts`.
- [ ] 2.3: Implement `ConcurrencyHelpersIos.kt`, `DiscoveryHelperIos.kt`, and `GameSocketIos.kt` in `feature/funhouse-engine-kotlin`.
- [ ] 2.4: Verify compilation: `./gradlew compileKotlinIosSimulatorArm64`.

### Phase 3: `:composeApp` & `iosApp` Xcode Wrapper
- [ ] 3.1: Add iOS framework target to `FunHouse/composeApp/build.gradle.kts`.
- [ ] 3.2: Implement `composeApp/src/iosMain/kotlin/Main.kt` with safe Koin initialization and `File(gamePath)`.
- [ ] 3.3: Generate `iosApp/` project (`iOSApp.swift`, `Info.plist`, `iosApp.xcodeproj`).
- [ ] 3.4: Verify framework embed task: `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`.

### Phase 4: Simulator Launch & Gameplay Verification
- [ ] 4.1: Boot simulator: `xcrun simctl boot "iPhone 17" && open -a Simulator`.
- [ ] 4.2: Build and run `iosApp` via `xcodebuild` targeting booted simulator.
- [ ] 4.3: Verify game category navigation (Skill, Arcade, Chatbots, Chance, Adventure).
- [ ] 4.4: Verify test games:
  - Compose games: Blackjack, Chess, Slot Machine, Classic Arcades.
  - Text games: Eliza, Adventure, FunHouse, Wander, Castle.
- [ ] 4.5: Verify dark mode toggle (`Cmd + Shift + A`) and landscape orientation (`Cmd + Arrow`).

### Phase 5: Google Mobile Ads (AdMob) iOS Integration (Monetization)
- [ ] 5.1: Add Google Mobile Ads SDK (`Google-Mobile-Ads-SDK`) via Swift Package Manager to `iosApp.xcodeproj`.
- [ ] 5.2: Configure `GADApplicationIdentifier` in `iosApp/Info.plist`.
- [ ] 5.3: Add `NSUserTrackingUsageDescription` (ATT prompt) and `SKAdNetworkItems` to `iosApp/Info.plist`.
- [ ] 5.4: Implement ATT authorization trigger in `iOSApp.swift`.
- [ ] 5.5: Wire `AdBannerView` bridge into `iosMain` and test with Google's iOS Test Ad Unit ID.

### Phase 6: App Store Connect & Distribution Preparation
- [ ] 6.1: Create Apple Developer Account ($99/year individual enrollment).
- [ ] 6.2: Create App record in App Store Connect with bundle ID `com.gepetto.gamescollection`.
- [ ] 6.3: Capture simulator screenshots (`Cmd + S`) for 6.7" iPhone and 12.9" iPad.
- [ ] 6.4: Complete Age Rating questionnaire (declaring Simulated Gambling for Casino games).
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

---

## 9. Bug, Blocker & Issue Tracker

| ID | Module / Component | Description | Status | Workaround / Resolution |
|---|---|---|---|---|
| BUG-001 | `gepetto-utils` | iOS targets not declared in `gepetto-utils` library | Open (Phase 0) | Implement iOS targets in dependency order and publish to `mavenLocal()`. |
| BUG-002 | `funhouse-engine-kotlin` | Platform-specific WebSocket & Concurrency code on JVM/Android | Open (Phase 2) | Implement Coroutine Channel queue, usleep, `objc_sync_enter/exit`, and stubs for discovery/sockets. |
| BUG-003 | App Store Review | Risk of rejection due to undeclared casino games | Documented (Phase 6) | Declare "Simulated Gambling" in Age Rating questionnaire (12+/17+ rating). |
| BUG-004 | App Store Review | Risk of rejection if "Tetric" infringes Tetris trademark | Documented (Phase 6) | Ensure `AppData.secretGamesEnabled = false` for release builds. |
| BUG-005 | Google AdMob iOS | Crash on launch if GADApplicationIdentifier is missing | Documented (Phase 5) | Ensure valid GADApplicationIdentifier key is present in Info.plist. |
| BUG-006 | `circum` | `circumIntentProcessor` signature mismatch | Resolved in Rev 4 | Use `@Composable actual inline fun <reified CIP : CircumViewModel> circumIntentProcessor(initialState: Any?, initialCommand: Any?): CIP` with `koinInject`. |
| BUG-007 | `shared:common` | 12+ games fail to compile due to missing `java.io.File` and `Thread.sleep` | Resolved in Rev 4 | Added iOS shims in `src/iosMain/kotlin/` with compiler flag `-Xallow-kotlin-package`. |
| BUG-008 | `composeApp` | `ClassCastException` on `AppData.gameFolderFile as File` | Resolved in Rev 4 | Set `AppData.gameFolderFile = File(gamePath)` using the iOS `File` shim instead of raw `String`. |
| BUG-009 | `composeApp` | `KoinAppAlreadyStartedException` when SwiftUI recreates `MainViewController` | Resolved in Rev 4 | Guard `startKoin` with `if (GlobalContext.getOrNull() == null)`. |
| BUG-010 | `gclog` | Package mismatch between `club.gepetto.gclog` and common `club.gepetto` | Resolved in Rev 4 | Use `package club.gepetto` for iOS actuals. |

*(Agents executing this plan: Add new entries above whenever a bug or obstacle is encountered during implementation)*
