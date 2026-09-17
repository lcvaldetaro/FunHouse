# Project Rules: FunHouse (Game Collection Multiplatform)

## General Principles
- **Scope**: Keep changes strictly focused on the requested tasks. Do not refactor unrelated games or modules without explicit confirmation.
- **Language**: Kotlin Multiplatform (KMP) using functional idioms and Compose Multiplatform.
- **Architecture**: MVI (Model-View-Intent) via the `circum` library (`CircumIntentProcessor`, `onIntentCommand`).
- **Code Owner Review**: NEVER commit or push to git without explicit instruction. Do not stage changes unless instructed.

## UI & UX Standards
- **Compose Multiplatform**: All UI must be written in Jetpack/Compose Multiplatform. Avoid platform-specific UI toolkits.
- **System Theming**: Use `sysBackgroundColor()` and `sysForegroundColor()` from `club.gepetto.composeutils`. Ensure all screens are readable in both light and dark modes.
- **Previews**: Composable functions should include `@PreviewLightDark` and landscape preview where appropriate, wrapped with `GcTheme {}`.
- **Localization**: Text must use Compose Multiplatform string resources (`Res.string.*`). User-facing markdown documents (e.g. `about_*.md`, `privacy_*.md`) must be localized across `en`, `de`, `es`, `fr`, `it`, and `pt`.
- **Adaptive Layout**: Support phone, tablet/iPad, and desktop screens using `WindowInsets`, `AdaptiveScaffold`, and adaptive navigation suites.

## Multiplatform Coding Standards
- **Common Logic**: Keep game logic in `commonMain`. Do not introduce JVM-only or Android-only dependencies (e.g. `java.io.File`, `android.content.Context`) directly into feature common code.
- **Logging**: Use `club.gepetto.GcLog` (e.g., `GcLog.d`, `GcLog.e`) for all logging. Never use `println` or Android `Log`.
- **Dependencies**: Always manage dependencies in `gradle/libs.versions.toml`. Choose KMP-compatible libraries.
- **Navigation**: Use Navigation 3 via `androidx.navigation3` and `gepetto-utils` wrappers.
- **Gradle Execution**: Always run `./gradlew` from the module or project root.

## Game Modules & Engines
- **Classic Migrations**: When porting or updating legacy game code, preserve original gameplay and logic. Never alter classic text adventure behavior without baselines.
- **FunHouse Engine**: Located in `feature/funhouse-engine-kotlin`. Used by *Island*, *FunHouse*, and *Space Station Aegis*. Handles CSV parsing and optional multiplayer networking over WebSockets and local discovery.
- **Wander Engine**: Located in `feature/wander-engine-kotlin`. Used by *Wander Castle*, *Wander Aldebaran*, *Wander Library*, and *Wander Logic Ops*.
- **Secret Games**: "Tetric" is hidden by default to avoid trademark concerns and is only unlocked via special Easter egg in Eliza or in non-release builds.
