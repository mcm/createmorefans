# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Create: More Fans is a Minecraft NeoForge mod (1.21.1) that provides a KubeJS API for creating custom Create fan processing types (like splashing/haunting) without directly extending Create's code. It handles integration with Create's fan processing system, recipe management, and JEI compatibility.

## Build Commands

- **Build:** `./gradlew build`
- **Run client:** `./gradlew runClient`
- **Run server:** `./gradlew runServer`
- **Run data generators:** `./gradlew runData`

Requires Java 21. Uses NeoForge Gradle (userdev plugin).

## Architecture

The mod has three layers:

1. **KubeJS Plugin Layer** (`kubejs/`): Entry point for modpack developers. `CreateMoreFansPlugin` implements `KubeJSPlugin` and fires a `CreateMoreFansRegistryEvent` during startup. This event exposes a `create()` method that returns a `KubeFanProcessingTypeBuilder` with a fluent API for configuring catalyst blocks/fluids, particles, entity effects, and JEI display options.

2. **Fan Processing Type** (`KubeFanProcessingType`): Implements Create's `FanProcessingType` interface. Delegates to the builder's configuration for `isValidAt` (catalyst detection via block/fluid tags), `canProcess`/`process` (recipe lookup), and particle/entity callbacks. Each type auto-registers a `RecipeType` and `RecipeSerializer` via inner builder classes (`RecipeTypeBuilder`, `SerializerBuilder`).

3. **JEI Integration** (`jei/`): `CreateMoreFansJEI` iterates all registered `FanProcessingType` entries, filters for `KubeFanProcessingType` instances, and dynamically creates JEI recipe categories using `KubeFanProcessingCategory`.

`KubeFanProcessingRecipe` extends Create's `StandardProcessingRecipe` with single-input, up to 12 outputs.

## Testing with a Headless Server

A test server lives in `test-server/` (gitignored). To set one up from scratch:

1. **Install NeoForge server:**
   ```
   cd test-server
   curl -LO "https://maven.neoforged.net/releases/net/neoforged/neoforge/<version>/neoforge-<version>-installer.jar"
   java -jar neoforge-<version>-installer.jar --install-server
   echo "eula=true" > eula.txt
   echo "-Xmx2G" > user_jvm_args.txt
   ```

2. **Download mods into `test-server/mods/`:** Create, KubeJS, Rhino, Flywheel, Vanillin, Ponder, and the built `createmorefans` jar. All are available from Maven (see `build.gradle` for repository URLs). Create version strings on maven use the format `6.0.X-BUILD` (e.g., `6.0.6-98`), not `6.0.X` directly.

3. **Enable RCON** in `server.properties` (`enable-rcon=true`, set `rcon.password`) to send commands programmatically (e.g., via Python `mcrcon` package).

4. **Add KubeJS test scripts** in `test-server/kubejs/startup_scripts/` and `test-server/kubejs/server_scripts/`.

5. **Run with a timeout** to prevent hangs on crash:
   ```
   timeout 180 bash run.sh nogui > /tmp/server.log 2>&1
   ```
   The server process does not always exit on crash — always use `timeout` or `pkill -f bootstraplauncher` to clean up.

### Important caveats

- **Client-only classes** (e.g., `GuiGraphics`, `GuiGameElement`) must never appear in method signatures or field types of classes that KubeJS/Rhino reflects on. Rhino calls `getDeclaredMethods()` on builder classes at startup; if any method signature references a client-only class, the entire class becomes invisible to KubeJS on dedicated servers. Use `Object` in signatures and cast inside method bodies instead.
- **Create API compatibility:** Create sometimes changes method signatures between minor versions (e.g., `RecipeApplier.applyRecipeOn` gained a parameter in 6.0.7). Use `MethodHandle` lookups when calling Create APIs that may differ across supported versions.
- When testing across Create versions, check the `neoforge.mods.toml` inside the Create jar for its minimum NeoForge version (`versionRange`) and Ponder version requirement.

## Key Dependencies

- NeoForge, Create (6.0.x), KubeJS, JEI — versions in `gradle.properties`
- All version properties and mod metadata are centralized in `gradle.properties`
