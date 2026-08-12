# Kaesekaestchen

![Kotlin](https://img.shields.io/badge/kotlin-2.4.10-blue.svg?logo=kotlin)
![JVM](https://img.shields.io/badge/-JVM-gray.svg?style=flat)
![WASM](https://img.shields.io/badge/-WASM-gray.svg?style=flat)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

A classic Dots'n'Boxes ("Käsekästchen") game built with Kotlin Multiplatform and Compose, faithful
to the original Android app: same rules, same handcrafted cheese and mouse artwork, same look and
feel.

<a href="https://play.google.com/store/apps/details?id=de.stefan_oltmann.kaesekaestchen">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/de_badge_web_generic.png"
       alt="Jetzt bei Google Play"
       height="80">
</a>

## Features

- Classic Dots'n'Boxes: draw lines, complete boxes, capture the most boxes.
- Single player against the computer, or two players on one device.
- Four board sizes from very small to large.
- Android, desktop (JVM), and web (WASM) targets.
- Localized UI (German and English).
- No permissions, no network, no tracking.

## Screenshots

The start screen, the game screen and the scoreboard look like the original app.

## Project Structure

- `app/src/commonMain/` - Shared Compose UI and game logic.
- `app/src/commonMain/iconResources/` - The handcrafted SVG artwork, converted to Compose
  `ImageVector`s by the Valkyrie Gradle plugin at build time.
- `app/src/androidMain/` - Android platform implementations for shared code.
- `app/src/jvmMain/` - Desktop entry point and platform integrations.
- `app/src/wasmJsMain/` - Web entry point and assets.
- `androidApp/src/main/` - Android entry points and resources.
- `icon/` - Platform icons generated from the handcrafted Play Store icon.

## Build and Run

Use the Gradle wrapper from the repo root. On Windows replace `./gradlew` with `.\gradlew.bat`.

- `./gradlew :app:jvmTest` - Run the unit tests (JVM).
- `./gradlew :app:run` - Run the desktop JVM app.
- `./gradlew :androidApp:assembleDebug` - Build the Android debug APK.
- `./gradlew :app:wasmJsBrowserDevelopmentRun` - Run the web dev server.
- `./gradlew :app:createDistributable` - Build the desktop distribution.
- `./gradlew :app:createMsix` - Build the Windows MSIX package.
- `./gradlew :app:detekt` - Run Detekt.
- `./gradlew :app:recordScreenshots` - Record the golden screenshots.
- `./gradlew :app:verifyScreenshots` - Verify the UI against the golden screenshots.

## Testing

The game logic, the computer AI, the hit-testing, and the real click interaction on the rendered
board are covered by unit tests in `commonTest` and `jvmTest`. Every screen is composed by
screenshot tests; goldens live in `app/src/jvmTest/screenshots/` (recorded in English and German)
and are verified pixel-exact via the dedicated Gradle tasks. Goldens are machine-specific, so record
them on the machine that verifies. Coverage is enforced by Kover on every `check` run.

## Architecture

- `model/` holds the pure game logic: `Board`, `Box`, `Line`, `Player`.
- `controller/` contains `GameLogic`, the turn engine with the computer opponent, and
  `PlayerManager`.
- `ui/` contains the Compose screens, the board rendering with tap hit-testing, and the theme.
- Settings are persisted through multiplatform settings; the keys match the original Android app, so
  existing players keep their settings.

## Licenses

Copyright (C) Stefan Oltmann.

This project is licensed under the GNU General Public License v3, see `LICENSE`.

The person and group icons are sourced from Google Material Design (Apache 2.0).
