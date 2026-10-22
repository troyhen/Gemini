# Gemini (Game Engine Mini) - Project Context

**Gemini** (*Game Engine Mini*) is a lightweight, fast 2D/3D-capable game engine built with **Kotlin Multiplatform (
KMP)** and **Compose Multiplatform**. It is designed to be embedded directly inside Compose UI hierarchies so games can
leverage standard Compose state, modifiers, input handling, and layouts across Android and Desktop JVM.

## Architecture & Tech Stack

- **Language:** Kotlin `2.4.21` (`kotlin.code.style=official`)
- **UI & Rendering:** JetBrains Compose Multiplatform `1.12.2` (Compose `Canvas`, `DrawScope`, 3D/2D `Matrix`
  transforms,
  `TextMeasurer`)
- **Targets:**
    - **Android:** `compileSdk = 37`, `minSdk = 23` (AGP `9.1.1`)
    - **Desktop / JVM:** Java 17 toolchain (`jvmTarget = "17"`)
- **Concurrency:** `kotlinx-coroutines` `1.11.0`
- **Image Loading:** Kamel (`media.kamel:kamel-image-default` `1.0.9`) + Ktor Client `3.6.0` (`okhttp` on Android,
  `java` on
  JVM)
- **Audio:** Platform-specific `expect class Sound` (`android.media.SoundPool` on Android,
  `javax.sound.sampled.AudioSystem` / `Clip` on JVM)
- **Dependency Management:** Gradle Kotlin DSL (`.kts`) with Version Catalog (`gradle/libs.versions.toml`)

## Module & Directory Structure

```text
.
├── gemini/                                # Core multiplatform game engine library (:gemini)
│   └── src/
│       ├── commonMain/kotlin/gemini/
│       │   ├── asset/                     # Asset lifecycle (Asset, Source, Image, expect Sound)
│       │   ├── engine/                    # Core runtime (Gemini composable, Stage, Scene, SceneScope, Camera, Collider)
│       │   ├── foundation/                # Game entities & primitives (Thing, MovingThing, Sprite, Atlas, Tile, Cycle, Particle, Background, FrameRate)
│       │   └── geometry/                  # Value-class math & transforms (Position, Location, Velocity, Space, Scale, Rotation, Angle, Rectangle, IntRectangle, World)
│       ├── androidMain/kotlin/gemini/
│       │   └── asset/Sound.android.kt     # Android SoundPool implementation
│       ├── jvmMain/kotlin/gemini/
│       │   └── asset/Sound.jvm.kt         # Desktop JVM Java Sound Clip implementation
│       └── commonTest/kotlin/gemini/      # Unit tests for geometry, engine, foundation, and assets
└── examples/                              # Sample games & demos (:examples)
    └── src/
        ├── commonMain/kotlin/examples/
        │   ├── action/Town.kt             # Sprite-sheet tilemap & walk-cycle demo
        │   ├── astroids/                  # Asteroids game (Game, Ship, Asteroid, Bullet, ShipState)
        │   ├── beads/                     # 2D bead & bowl physics/collision simulation
        │   ├── checkers/                  # Turn-based Checkers board game (event-driven stepping)
        │   ├── sandbox/                   # Interactive sandbox (shapes, remote images, sound, pointer input)
        │   └── starfield/                 # 3D perspective starfield simulation
        ├── androidMain/                   # Android activities (Asteroids, Beads, Checkers, Sandbox, StarField, Town) & assets
        └── desktopMain/                   # Desktop singleWindowApplication entry points & resources
```

## Building and Running

Use the Gradle wrapper (`./gradlew`) from the repository root.

### General Commands

| Task                             | Command                             |
|:---------------------------------|:------------------------------------|
| **Build Engine Library**         | `./gradlew :gemini:assemble`        |
| **Build Android Examples APK**   | `./gradlew :examples:assembleDebug` |
| **Install Android Examples APK** | `./gradlew :examples:installDebug`  |
| **Run All Tests**                | `./gradlew test`                    |
| **Clean Build Artifacts**        | `./gradlew clean`                   |

### Running Desktop Examples

Desktop entry points live in `examples/src/desktopMain/kotlin/examples/` and are launched via `:examples:desktopRun` by
passing `-DmainClass`:

| Example       | Command                                                           |
|:--------------|:------------------------------------------------------------------|
| **Asteroids** | `./gradlew :examples:desktopRun -DmainClass=examples.AsteroidsKt` |
| **Beads**     | `./gradlew :examples:desktopRun -DmainClass=examples.BeadsKt`     |
| **Checkers**  | `./gradlew :examples:desktopRun -DmainClass=examples.CheckersKt`  |
| **Sandbox**   | `./gradlew :examples:desktopRun -DmainClass=examples.SandboxKt`   |
| **Starfield** | `./gradlew :examples:desktopRun -DmainClass=examples.StarfieldKt` |
| **Town**      | `./gradlew :examples:desktopRun -DmainClass=examples.TownKt`      |

## Core Engine Concepts & Development Conventions

### 1. Scene Construction & DSL

- Scenes are created declaratively using
  `@Composable fun rememberScene(vararg keys: Any, builder: SceneScope.() -> Unit): Scene`, which automatically releases
  loaded assets when disposed.
- Render a scene using the `@Composable fun Gemini(...)` composable:
  ```kotlin
  val game = rememberScene {
      camera.orthographic()
      background(Color.Black)
      frameRate(Color.White, Pivot.NorthEast)
      // add entities via SceneScope extensions
  }
  Gemini(modifier) {
      scene = game
  }
  ```
- Expose new game entities via `SceneScope` builder extension functions that instantiate the `Thing`, call `add(it)`,
  and return the instance:
  ```kotlin
  fun SceneScope.myEntity(...): MyEntity = MyEntity(...).also { add(it) }
  ```

### 2. Game Loop & Execution Modes (`Stage`)

- **Continuous Loop (Default):** `Gemini(startImmediately = true)` increments `frame` after each draw pass, triggering
  `act(elapsed: Duration)` on `Dispatchers.Default` followed by pairwise `Collider` overlap detection.
    - On Android, pause and resume the stage in `Activity.onPause()` / `onResume()` using `Stage.instance?.stop()` and
      `Stage.instance?.start()`.
- **Event-Driven / Turn-Based Mode:** Pass `startImmediately = false` to `Gemini(...)` (e.g., `Checkers.kt`). The loop
  stays idle until `Stage.instance?.step()` (or `add`/`remove`) is called to advance a single frame and recompose.

### 3. Entities, Drawing, and Camera Layers (`Thing`, `MovingThing`, `Camera`)

- **`Thing`:** Base entity holding `position: Position`.
    - Override `DrawScope.drawRelative()` when drawing in the entity's local coordinate space (after
      `position.transform()` applies translation, 3D rotation, scale, and pivot offset).
    - Override `DrawScope.draw()` only when drawing directly in world/screen coordinates without the automatic
      `position.transform()` wrapper.
- **`MovingThing`:** Extends `Thing` with `velocity: Velocity` and `spin: Angle`. Automatically updates
  `position.location` and `position.rotation.r` in `act(elapsed)`. Provides `wrap(world)` and `bounce(world)` boundary
  helpers.
- **Camera Modes & Render Order:**
    - `camera.default()`: No projection matrix; uses raw canvas pixel coordinates (`0..width` by `0..height`).
    - `camera.orthographic(...)` / `camera.perspective(...)`: Applies 3D projection (defaults to `-1f..1f` normalized
      world coordinates) and computes aspect-ratio-adjusted bounds in `Stage.visible`.
    - `Stage` renders in three passes:
        1. Entities implementing `BeforeCamera` (e.g., `Background`, `CheckerBoard`) in screen space.
        2. Standard entities inside `withTransform({ camera.run { transform() } })`.
        3. Entities implementing `AfterCamera` (e.g., `FrameRate` HUD overlays) in screen space.

### 4. Collisions (`Collider`)

- Entities participating in collision detection implement `Collider`:
  ```kotlin
  override fun collideWith(collider: Collider): Boolean
  ```
- `Stage` checks axis-aligned bounding box overlaps (`position.rectangle(...)`) for all `Collider` pairs
  `(actor, other)`. If `actor.collideWith(other)` returns `false`, `other.collideWith(actor)` is invoked as a fallback.

### 5. Geometry & Zero-Allocation Hot Paths

- Most geometry primitives in `gemini.geometry` (`Location`, `Velocity`, `Space`, `Scale`, `Rotation`, `Rectangle`,
  `IntRectangle`, `World`) are `@JvmInline value class`es backed by `FloatArray` or `IntArray`, and `Angle` is a
  `@JvmInline value class` wrapping `Float` degrees.
- **Avoid per-frame allocations** in `act()` and `draw()`:
    - Mutate existing geometry state in-place using `.set(...)`, `.setFrom(...)`, `.move(...)`,
      `.change(velocity, elapsed)`, `.negate()`, `.normalize()`, and compound assignment operators (`+=`, `-=`, `*=`,
      `/=`).
    - Pre-allocate scratch `Rectangle` or `Velocity` fields on entity classes instead of instantiating them inside
      `act()`, `draw()`, or `collideWith()`.
    - Use `fastForEach` and `fastForEachIndexed` from `gemini.engine` when iterating over `List` or `Array` in hot
      paths.

### 6. Assets (`Image`, `Sound`, `Atlas`, `Source`)

- Convert paths, URLs, files, or resource IDs to `Source` via `.asSource` (`asset:name`, `assets://name`,
  `resource:path`, `file:...`, `http(s)://...`).
- Register assets through `Scene` methods (`image(source)`, `sound(source)`, `tileMap(source, tileSize, inset)`,
  `imageMap(source, find)`) so `Stage.load(scene)` loads them and `Scene.release()` disposes them.
- **Desktop Resources:** Wrap the root composable in `CompositionLocalProvider(LocalKamelConfig provides desktopConfig)`
  with `resourcesFetcher()` enabled when loading `resource:` images on Desktop.
- **Android Audio:** Set `Sound.context = this` in `Activity.onCreate()` before loading `asset:` or resource audio on
  Android.
