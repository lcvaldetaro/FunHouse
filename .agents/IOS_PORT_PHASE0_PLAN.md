# Phase 0 Sub-Plan: gepetto-utils iOS Target Support

> **Parent Plan**: [IOS_PORT_PLAN.md](file:///Users/luizvaldetaro/valdetaro/FunHouse/.agents/IOS_PORT_PLAN.md)  
> **Scope**: Phase 0 only — Add iOS targets to all 4 `gepetto-utils` libraries and publish to `mavenLocal()`  
> **Project Path**: `/Users/luizvaldetaro/valdetaro/gepetto-utils`  
> **Build Order**: `circum` → `gepetto-utils` → `gclog` → `ads-lib` (strict topological)

> [!IMPORTANT]
> **Kotlin 2.4.20 uses the default hierarchy template.** When you declare `iosX64()`, `iosArm64()`, and `iosSimulatorArm64()`, the shared `iosMain` source set is **automatically created**. Place iOS actual files under `src/iosMain/kotlin/`. Reference `iosMain` in `sourceSets` with `val iosMain by getting { ... }`.

---

## Pre-Flight Check

Before starting, verify the environment:

```bash
cd /Users/luizvaldetaro/valdetaro/gepetto-utils
java -version          # Must be JDK 21
./gradlew --version    # Must be Gradle 9.7.1
```

---

## Step 0.0: Add `ktor-client-darwin` to `libs.versions.toml`

> [!IMPORTANT]
> This goes into **gepetto-utils's** toml, not FunHouse's.

#### [MODIFY] [libs.versions.toml](file:///Users/luizvaldetaro/valdetaro/gepetto-utils/gradle/libs.versions.toml)

Add this line in the `[libraries]` section, near the other ktor entries:

```toml
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktorClientCore" }
```

This uses the existing `ktorClientCore = "3.6.0"` version reference.

---

## Step 0.1: `:circum` Module

### 0.1.1 — Modify `build.gradle.kts`

#### [MODIFY] [circum/build.gradle.kts](file:///Users/luizvaldetaro/valdetaro/gepetto-utils/circum/build.gradle.kts)

Add iOS targets after the `wasmJs` block (after line 36):

```kotlin
    // === iOS Targets ===
    iosX64()
    iosArm64()
    iosSimulatorArm64()
```

No additional source set dependencies or `iosMain` block needed — circum's iOS actual only uses `platform.Foundation.*` and existing commonMain dependencies (Compose runtime, Koin, Coroutines), all of which support iOS.

### 0.1.2 — Create iOS Actual

#### [NEW] `circum/src/iosMain/kotlin/club/gepetto/circum/CircumIos.kt`

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

### 0.1.3 — Verify

```bash
cd /Users/luizvaldetaro/valdetaro/gepetto-utils
./gradlew :circum:compileKotlinIosSimulatorArm64
```

---

## Step 0.2: `:gepetto-utils` Module

This is the largest module with **27 expect declarations** that need iOS actuals.

### 0.2.1 — Modify `build.gradle.kts`

#### [MODIFY] [gepetto-utils/build.gradle.kts](file:///Users/luizvaldetaro/valdetaro/gepetto-utils/gepetto-utils/build.gradle.kts)

Add iOS targets after the `wasmJs` block (after the `wasmJs { browser() }` closing brace):

```kotlin
    // === iOS Targets ===
    iosX64()
    iosArm64()
    iosSimulatorArm64()
```

Add iosMain source set dependencies inside the `sourceSets { }` block (after `desktopMain`):

```kotlin
        val iosMain by getting {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
```

### 0.2.2 — Create iOS Actual Files (11 files)

All files go under `gepetto-utils/src/iosMain/kotlin/`.

---

#### [NEW] File 1: `club/gepetto/composeutils/PlatformFile.ios.kt`

```kotlin
package club.gepetto.composeutils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
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

    @OptIn(ExperimentalForeignApi::class)
    actual fun writeBytes(bytes: ByteArray) {
        bytes.usePinned { pinned ->
            val nsData = NSData.create(
                bytes = pinned.addressOf(0),
                length = bytes.size.toULong()
            )
            nsData.writeToFile(path, atomically = true)
        }
    }

    actual fun mkdir(): Boolean =
        NSFileManager.defaultManager.createDirectoryAtPath(
            path, withIntermediateDirectories = false, attributes = null, error = null
        )

    actual fun mkdirs(): Boolean =
        NSFileManager.defaultManager.createDirectoryAtPath(
            path, withIntermediateDirectories = true, attributes = null, error = null
        )

    actual fun length(): Long {
        val attrs = NSFileManager.defaultManager.attributesOfItemAtPath(path, error = null)
            ?: return 0L
        return (attrs[NSFileSize] as? NSNumber)?.longValue ?: 0L
    }

    actual fun lastModified(): Long {
        val attrs = NSFileManager.defaultManager.attributesOfItemAtPath(path, error = null)
            ?: return 0L
        val date = attrs[NSFileModificationDate] as? NSDate ?: return 0L
        return (date.timeIntervalSince1970 * 1000.0).toLong()
    }

    actual fun delete(): Boolean =
        NSFileManager.defaultManager.removeItemAtPath(path, error = null)
}
```

> [!NOTE]
> `writeBytes` uses `NSData.create(bytes:length:)` instead of the string-roundtrip in the original plan, preventing binary data corruption.

---

#### [NEW] File 2: `club/gepetto/composeutils/PlatformBitmap.ios.kt`

```kotlin
package club.gepetto.composeutils

import androidx.compose.ui.graphics.ImageBitmap

actual class PlatformBitmap(val imageBitmap: ImageBitmap)

actual fun PlatformBitmap.toImageBitmap(): ImageBitmap = this.imageBitmap
```

---

#### [NEW] File 3: `club/gepetto/composeutils/PlatformHttpClient.ios.kt`

```kotlin
package club.gepetto.composeutils

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin

actual fun createPlatformHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Darwin) { block() }
}
```

---

#### [NEW] File 4: `club/gepetto/composeutils/GcCurrentTimeMillis.ios.kt`

```kotlin
package club.gepetto.composeutils

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun gcCurrentTimeMillis(): Long =
    (NSDate().timeIntervalSince1970 * 1000.0).toLong()
```

---

#### [NEW] File 5: `club/gepetto/composeutils/Actuals.ios.kt`

Consolidates: `Context`, `QrCodeView`, image creation stubs, `GcCoil2Image`, `isAndroidPlatform`, `BackHandler`, `textAsBitmap`.

```kotlin
package club.gepetto.composeutils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale

actual abstract class Context

@Composable
actual fun QrCodeView(data: String, modifier: Modifier) {
    Box(modifier)
}

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
    return PlatformBitmap(ImageBitmap(1, 1))
}
```

---

#### [NEW] File 6: `club/gepetto/composeutils/GcQrCodeScanner.ios.kt`

```kotlin
package club.gepetto.composeutils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun GcQrCodeScanner(
    modifier: Modifier,
    processResult: (String) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Text("QR Scanner is not supported on iOS yet", color = Color.White)
    }
}
```

---

#### [NEW] File 7: `club/gepetto/composeutils/image/ImageActuals.ios.kt`

Consolidates: `LegacyImageResource`, `GcFullImagePopup`, `gCnewImageLoader`.

```kotlin
package club.gepetto.composeutils.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import club.gepetto.composeutils.PlatformBitmap
import coil3.ImageLoader
import coil3.PlatformContext
import org.jetbrains.compose.resources.DrawableResource

@Composable
actual fun LegacyImageResource(
    resId: Int,
    modifier: Modifier,
    contentDescription: String?,
    contentScale: ContentScale
) {
    // iOS stub — legacy Android resource IDs are not applicable
}

@Composable
actual fun GcFullImagePopup(
    imageFile: String?,
    imageResource: Int,
    imageResourceRes: DrawableResource?,
    imageBitmap: PlatformBitmap?,
    files: Array<String>?,
    folder: String?,
    urlForImages: String?,
    errorImage: DrawableResource?,
    fallbackImage: DrawableResource?,
    placeHolderImage: DrawableResource?,
    onDismiss: () -> Unit,
) {
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        GcFullImageCarousel(
            imageFile = imageFile,
            imageResource = imageResource,
            imageResourceRes = imageResourceRes,
            imageBitmap = imageBitmap,
            files = files,
            folder = folder,
            urlForImages = urlForImages,
            onDismiss = onDismiss
        )
    }
}

actual fun gCnewImageLoader(context: Any?): ImageLoader {
    return ImageLoader.Builder(PlatformContext.INSTANCE).build()
}
```

> [!NOTE]
> `GcFullImagePopup` delegates to `GcFullImageCarousel` (defined in commonMain), matching the desktop and wasmJs pattern. `gCnewImageLoader` uses Coil3's `PlatformContext.INSTANCE` which supports iOS.

---

#### [NEW] File 8: `club/gepetto/composeutils/webpage/WebComposeUtils.ios.kt`

```kotlin
package club.gepetto.composeutils.webpage

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun GcHtmlView(htmlData: String, modifier: Modifier) {
    Box(modifier) { Text(htmlData) }
}

@Composable
actual fun GcHtmlText(htmlText: String, modifier: Modifier) {
    Box(modifier) { Text(htmlText) }
}

@Composable
actual fun GcHtmlFile(htmlFolder: String, htmlFilename: String, modifier: Modifier) {
    Box(modifier)
}
```

---

#### [NEW] File 9: `club/gepetto/utils/Dispatcher.ios.kt`

```kotlin
package club.gepetto.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
```

---

#### [NEW] File 10: `club/gepetto/utils/Utils.ios.kt`

```kotlin
package club.gepetto.utils

import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance

private val synthesizer = AVSpeechSynthesizer()

actual fun gCSpeak(text: String) {
    if (text.isBlank()) return
    synthesizer.speakUtterance(AVSpeechUtterance(string = text))
}

actual fun isRunningOnChromebook(context: Any): Boolean = false
```

---

#### [NEW] File 11: `club/gepetto/utils/GcAppInfo.ios.kt`

```kotlin
package club.gepetto.utils

actual object GcAppInfo {
    actual var application_Context: Any? = null
    actual var versionCode: Long? = 1L
    actual var versionName: String? = "1.0"
    actual var ttsHandle: Any? = null
    actual var appPackageFolder: String = ""
    actual var releaseVersion: Boolean = false
}
```

> [!IMPORTANT]
> All **6** expect fields are present. Unlike the desktop actual (which adds extra non-expect fields `filesDir` and `appDirectoryFile`), the iOS actual only needs the expect-mandated fields.

---

### 0.2.3 — Check for `java/io/File.kt` shim need

The wasmJs source set includes a `java/io/File.kt` shim. Before compiling, check if any `gepetto-utils` commonMain code references `java.io.*` directly:

```bash
cd /Users/luizvaldetaro/valdetaro/gepetto-utils
grep -r "import java\." gepetto-utils/src/commonMain/kotlin/ || echo "No java imports found"
```

If java.io references exist, copy the wasmJs shim pattern to `gepetto-utils/src/iosMain/kotlin/java/io/File.kt`.

### 0.2.4 — Verify

```bash
cd /Users/luizvaldetaro/valdetaro/gepetto-utils
./gradlew :gepetto-utils:compileKotlinIosSimulatorArm64
```

---

## Step 0.3: `:gclog` Module

### 0.3.1 — Modify `build.gradle.kts`

#### [MODIFY] [gclog/build.gradle.kts](file:///Users/luizvaldetaro/valdetaro/gepetto-utils/gclog/build.gradle.kts)

Add iOS targets after the `wasmJs` block:

```kotlin
    // === iOS Targets ===
    iosX64()
    iosArm64()
    iosSimulatorArm64()
```

No additional source set dependencies needed.

### 0.3.2 — Create iOS Actual

#### [NEW] `gclog/src/iosMain/kotlin/club/gepetto/PlatformIos.kt`

> [!CAUTION]
> Package **must** be `club.gepetto` — not `club.gepetto.gclog`. The expects in [GcLog.kt](file:///Users/luizvaldetaro/valdetaro/gepetto-utils/gclog/src/commonMain/kotlin/club/gepetto/GcLog.kt) are declared in `package club.gepetto`.

```kotlin
package club.gepetto

import platform.Foundation.NSLog

internal actual fun formatString(pattern: String, args: Array<out Any?>): String =
    args.fold(pattern) { acc, arg -> acc.replaceFirst("%s", arg.toString()) }

internal actual fun getStackTag(): String? = null

internal actual fun platformLog(priority: Int, tag: String?, message: String, t: Throwable?) {
    val level = when (priority) {
        2 -> "V"; 3 -> "D"; 4 -> "I"; 5 -> "W"; 6 -> "E"; else -> "LOG"
    }
    val logTag = tag ?: "GcLog"
    NSLog("[$level/$logTag] $message")
    t?.let { NSLog("  Exception: ${it.message}") }
}
```

### 0.3.3 — Verify

```bash
cd /Users/luizvaldetaro/valdetaro/gepetto-utils
./gradlew :gclog:compileKotlinIosSimulatorArm64
```

---

## Step 0.4: `:ads-lib` Module

### 0.4.1 — Modify `build.gradle.kts`

#### [MODIFY] [ads-lib/build.gradle.kts](file:///Users/luizvaldetaro/valdetaro/gepetto-utils/ads-lib/build.gradle.kts)

Add iOS targets after the `wasmJs` block:

```kotlin
    // === iOS Targets ===
    iosX64()
    iosArm64()
    iosSimulatorArm64()
```

No additional source set dependencies needed (ads are stubbed on iOS for now).

### 0.4.2 — Create iOS Actual Files (2 files)

---

#### [NEW] File 1: `ads-lib/src/iosMain/kotlin/club/gepetto/gcadslib/Actuals.ios.kt`

Implements: `Bundle`, `initMobileAds`, `initAnalytics`, `initAnalyticsAndAds`, `checkFirstRun`, `AnalyticsTracker`.

```kotlin
package club.gepetto.gcadslib

import club.gepetto.composeutils.Context

actual class Bundle actual constructor() {
    private val map = mutableMapOf<String, Any>()
    actual fun putInt(key: String?, value: Int) { key?.let { map[it] = value } }
    actual fun putString(key: String?, value: String?) {
        if (key != null && value != null) map[key] = value
    }
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

---

#### [NEW] File 2: `ads-lib/src/iosMain/kotlin/club/gepetto/gcadslib/ui/ActualsUi.ios.kt`

Implements all UI ad composables as no-op stubs.

```kotlin
package club.gepetto.gcadslib.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import club.gepetto.composeutils.Context

actual abstract class NativeAd

@Composable actual fun NativeAdViewComposeQuarterScreen(
    nativeAd: NativeAd, modifier: Modifier, title: String,
    darkMode: Boolean?, refreshTimer: Int
) { Box(modifier) }

@Composable actual fun NativeAdViewComposeEightScreen(
    nativeAd: NativeAd, modifier: Modifier, title: String,
    darkMode: Boolean?, refreshTimer: Int
) { Box(modifier) }

@Composable actual fun NativeAdViewComposeHalfScreen(
    nativeAd: NativeAd, modifier: Modifier, rightPane: Boolean,
    title: String, darkMode: Boolean?, refreshTimer: Int
) { Box(modifier) }

@Composable actual fun NativeAdViewComposeBanner(
    nativeAd: NativeAd, modifier: Modifier, title: String,
    darkMode: Boolean?, refreshTimer: Int
) { Box(modifier) }

@Composable actual fun NativeAdViewComposeLargeBanner(
    nativeAd: NativeAd, modifier: Modifier, title: String,
    darkMode: Boolean?, refreshTimer: Int
) { Box(modifier) }

@Composable actual fun NativeAdViewComposeFullBanner(
    nativeAd: NativeAd, modifier: Modifier, title: String,
    darkMode: Boolean?, refreshTimer: Int
) { Box(modifier) }

@Composable actual fun NativeAdViewComposeLeaderboard(
    nativeAd: NativeAd, modifier: Modifier, title: String,
    darkMode: Boolean?, refreshTimer: Int
) { Box(modifier) }

@Composable actual fun NativeAdViewComposeMediumRectangle(
    nativeAd: NativeAd, modifier: Modifier, title: String,
    darkMode: Boolean?, refreshTimer: Int
) { Box(modifier) }

@Composable actual fun AdNative(
    modifier: Modifier, adUnit: String, rightPane: Boolean,
    title: String, darkMode: Boolean?, refreshTimer: Int,
    adImpressionTag: String, adUnitTag: String, adErrorTag: String,
    onAdLoaded: () -> Unit, onAdImpression: () -> Unit,
    onAdClicked: () -> Unit, onError: () -> Unit
) { Box(modifier) }

@Composable actual fun AdNativeBanner(
    modifier: Modifier, adUnit: String, title: String,
    darkMode: Boolean?, refreshTimer: Int,
    adImpressionTag: String, adUnitTag: String, adErrorTag: String,
    onAdLoaded: () -> Unit, onAdImpression: () -> Unit,
    onAdClicked: () -> Unit, onError: () -> Unit
) { Box(modifier) }

@Composable actual fun AdNativeLargeBanner(
    modifier: Modifier, adUnit: String, title: String,
    darkMode: Boolean?, refreshTimer: Int,
    adImpressionTag: String, adUnitTag: String, adErrorTag: String,
    onAdLoaded: () -> Unit, onAdImpression: () -> Unit,
    onAdClicked: () -> Unit, onError: () -> Unit
) { Box(modifier) }

@Composable actual fun AdNativeFullBanner(
    modifier: Modifier, adUnit: String, title: String,
    darkMode: Boolean?, refreshTimer: Int,
    adImpressionTag: String, adUnitTag: String, adErrorTag: String,
    onAdLoaded: () -> Unit, onAdImpression: () -> Unit,
    onAdClicked: () -> Unit, onError: () -> Unit
) { Box(modifier) }

@Composable actual fun AdNativeLeaderboard(
    modifier: Modifier, adUnit: String, title: String,
    darkMode: Boolean?, refreshTimer: Int,
    adImpressionTag: String, adUnitTag: String, adErrorTag: String,
    onAdLoaded: () -> Unit, onAdImpression: () -> Unit,
    onAdClicked: () -> Unit, onError: () -> Unit
) { Box(modifier) }

@Composable actual fun AdNativeMediumRectangle(
    modifier: Modifier, adUnit: String, title: String,
    darkMode: Boolean?, refreshTimer: Int,
    adImpressionTag: String, adUnitTag: String, adErrorTag: String,
    onAdLoaded: () -> Unit, onAdImpression: () -> Unit,
    onAdClicked: () -> Unit, onError: () -> Unit
) { Box(modifier) }

@Composable actual fun AdBanner(
    modifier: Modifier, adUnit: String, adSize: AdBannerSize,
    adImpressionTag: String, adErrorTag: String, adClickTag: String,
    adSizeTag: String, adWidthDp: Dp?,
    onAdLoaded: () -> Unit, onAdImpression: () -> Unit,
    onAdClicked: () -> Unit, onError: () -> Unit
) { Box(modifier) }

@Composable actual fun AdBannerAdaptive(
    adUnitId: String, modifier: Modifier, adWidthDp: Dp?
) { Box(modifier) }

@Composable actual fun AdBannerCard(
    modifier: Modifier, adSize: AdBannerSize
) { Box(modifier) }

@Composable actual fun AdBannerBox(
    modifier: Modifier, adSize: AdBannerSize
) { Box(modifier) }

@Composable actual fun GcAd(
    modifier: Modifier, rightPane: Boolean, adSize: AdBannerSize,
    title: String, darkMode: Boolean?, refreshTimer: Int,
    usingNativeAd: Boolean, adImpressionTag: String, adErrorTag: String,
    adClickTag: String, adSizeTag: String, adUnitTag: String,
    adUnitId: String, adWidthDp: Dp?,
    onAdLoaded: () -> Unit, onAdImpression: () -> Unit,
    onAdClicked: () -> Unit, onError: () -> Unit
) { Box(modifier) }

actual object AdInterstitial {
    actual fun load(context: Context) {}
    actual fun show(context: Context, onAdDismissed: () -> Unit) { onAdDismissed() }
    actual fun isReady(): Boolean = false
    actual fun isLoading(): Boolean = false
}

@Composable actual fun InterstitialAd(content: @Composable () -> Unit) { content() }
```

### 0.4.3 — Verify

```bash
cd /Users/luizvaldetaro/valdetaro/gepetto-utils
./gradlew :ads-lib:compileKotlinIosSimulatorArm64
```

---

## Step 0.5: Publish All to `mavenLocal()`

```bash
cd /Users/luizvaldetaro/valdetaro/gepetto-utils
./gradlew publishToMavenLocal
```

> [!IMPORTANT]
> This publishes **all 4 libraries** with their new iOS artifacts. Verify that the iOS metadata/klib files appear:
> ```bash
> ls ~/.m2/repository/club/gepetto/circum/*/  | grep ios
> ls ~/.m2/repository/club/gepetto/gepetto-utils/*/  | grep ios
> ls ~/.m2/repository/club/gepetto/gclog/*/  | grep ios
> ls ~/.m2/repository/club/gepetto/gcadslib/*/  | grep ios
> ```

---

## Step 0.6: Verify Existing Targets Are Not Broken

> [!WARNING]
> Per coding rule #9 in [AGENTS.md](file:///Users/luizvaldetaro/valdetaro/.agents/AGENTS.md): *"When making changes to the Library, build all targets for the Lap Counter, Toy Collection, FunHouse and Scanner."*

After publishing, verify that existing Android, Desktop, and WasmJs targets still compile:

```bash
cd /Users/luizvaldetaro/valdetaro/gepetto-utils
./gradlew compileKotlinAndroid compileKotlinDesktop compileKotlinWasmJs
```

If all pass, Phase 0 is complete.

---

## File Summary

| Step | Module | Action | File Path |
|---|---|---|---|
| 0.0 | gepetto-utils (root) | MODIFY | `gradle/libs.versions.toml` |
| 0.1.1 | circum | MODIFY | `circum/build.gradle.kts` |
| 0.1.2 | circum | NEW | `circum/src/iosMain/kotlin/club/gepetto/circum/CircumIos.kt` |
| 0.2.1 | gepetto-utils | MODIFY | `gepetto-utils/build.gradle.kts` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/composeutils/PlatformFile.ios.kt` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/composeutils/PlatformBitmap.ios.kt` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/composeutils/PlatformHttpClient.ios.kt` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/composeutils/GcCurrentTimeMillis.ios.kt` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/composeutils/Actuals.ios.kt` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/composeutils/GcQrCodeScanner.ios.kt` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/composeutils/image/ImageActuals.ios.kt` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/composeutils/webpage/WebComposeUtils.ios.kt` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/utils/Dispatcher.ios.kt` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/utils/Utils.ios.kt` |
| 0.2.2 | gepetto-utils | NEW | `gepetto-utils/src/iosMain/kotlin/club/gepetto/utils/GcAppInfo.ios.kt` |
| 0.3.1 | gclog | MODIFY | `gclog/build.gradle.kts` |
| 0.3.2 | gclog | NEW | `gclog/src/iosMain/kotlin/club/gepetto/PlatformIos.kt` |
| 0.4.1 | ads-lib | MODIFY | `ads-lib/build.gradle.kts` |
| 0.4.2 | ads-lib | NEW | `ads-lib/src/iosMain/kotlin/club/gepetto/gcadslib/Actuals.ios.kt` |
| 0.4.2 | ads-lib | NEW | `ads-lib/src/iosMain/kotlin/club/gepetto/gcadslib/ui/ActualsUi.ios.kt` |

**Totals**: 5 files modified, 15 new files created.

---

## Potential Blockers

| Risk | Description | Mitigation |
|---|---|---|
| Missing `java/io/File.kt` shim | If gepetto-utils commonMain references `java.io.File` directly (wasmJs has this shim), iOS compilation will fail | Run `grep -r "import java\." gepetto-utils/src/commonMain/` and copy wasmJs shim if needed |
| Signature drift | If any expect signature changed since this plan was written, actuals won't match | Compiler errors will pinpoint exact mismatches — fix the actual signature |
| Coil3 iOS support | `PlatformContext.INSTANCE` in `gCnewImageLoader` requires Coil 3.x iOS support | Already using Coil 3.6.2 which supports iOS via Compose Multiplatform |
| CMake native build | gepetto-utils has CMake/JNI code in `android {}` block | Only triggers for Android target — does not affect iOS |
