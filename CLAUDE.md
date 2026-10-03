# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Create: More Fans is a Minecraft Forge mod (1.20.1) that provides a KubeJS API for creating custom Create fan processing types (like splashing/haunting) without directly extending Create's code. It handles integration with Create's fan processing system, recipe management, and JEI compatibility.

## Build Commands

- **Build:** `./gradlew build`
- **Run client:** `./gradlew runClient`
- **Run server:** `./gradlew runServer`
- **Run data generators:** `./gradlew runData`

Requires Java 17. Uses ModDevGradle's `legacyforge` plugin (the same setup Create uses on 1.20.1). Mod dependencies must be declared with `modImplementation`/`modCompileOnly`/`modRuntimeOnly` so they get remapped from SRG.

The built jar is `build/libs/<mod_id>-<minecraft_version>-<mod_version>.jar` (e.g. `createmorefans-1.20.1-0.1.1.jar`).

## CI / Release

- **Branches per Minecraft version:** `main` targets the newest supported Minecraft version (currently 1.21.1). Older versions live on branches named after the version (e.g. `1.20.1`). When a new Minecraft version arrives, branch the current release off `main` as `<old version>` (e.g. `1.21.1`) and move `main` to the new version. **This is the `1.20.1` branch**: open PRs for 1.20.1 work against `1.20.1`, and port relevant fixes from `main` here.
- **Release tags** are `v<mod_version>-mc<minecraft_version>` (e.g. `v0.1.1-mc1.21.1`), created on the branch for that Minecraft version. The same `mod_version` can be released for several Minecraft versions under distinct tags.
- `.github/workflows/ci.yml` runs on pushes to and PRs targeting `main` or a version branch (`[0-9]*.[0-9]*`), weekly (default branch only), and on manual dispatch (not on tags). The `CI ✅` job is the single gate for build, Semgrep, and secret scanning. Third-party actions are pinned to commit SHAs; keep them pinned when updating.
- `.github/workflows/publish-curseforge.yml` runs on `v*` tags. The tag's mod version and Minecraft version must match `mod_version` and `minecraft_version` in `gradle.properties` at the tagged commit, and the CurseForge changelog comes from the matching `## [x.y.z]` section of `CHANGELOG.md` (or a generic fallback linking to the GitHub release if that section is absent). Uploads must list an `environment:Client`/`environment:Server` game version or CurseForge rejects them (errorCode 1021).

## Architecture

The mod has three layers:

1. **KubeJS Plugin Layer** (`kubejs/`): Entry point for modpack developers. `CreateMoreFansPlugin` implements `KubeJSPlugin` and fires a `CreateMoreFansRegistryEvent` during startup. This event exposes a `create()` method that returns a `KubeFanProcessingTypeBuilder` with a fluent API for configuring catalyst blocks/fluids, particles, entity effects, and JEI display options.

2. **Fan Processing Type** (`KubeFanProcessingType`): Implements Create's `FanProcessingType` interface. Delegates to the builder's configuration for `isValidAt` (catalyst detection via block/fluid tags), `canProcess`/`process` (recipe lookup), and particle/entity callbacks. Each type auto-registers a `RecipeType` and `RecipeSerializer` via inner builder classes (`RecipeTypeBuilder`, `SerializerBuilder`).

3. **JEI Integration** (`jei/`): `CreateMoreFansJEI` iterates all registered `FanProcessingType` entries, filters for `KubeFanProcessingType` instances, and dynamically creates JEI recipe categories using `KubeFanProcessingCategory`.

`KubeFanProcessingRecipe` extends Create's `ProcessingRecipe<RecipeWrapper>` with single-input, up to 12 outputs.

On 1.20.1, Create's JEI `CategoryBuilder` is private to `CreateJEI`, so `CreateMoreFansJEI` builds `CreateRecipeCategory.Info` directly. KubeJS 2001 has no `TagKey` type wrapper, so the catalyst tag setters take a `String`.

## Testing with a Headless Server

A test server lives in `test-server/` (gitignored). To set one up from scratch:

1. **Install Forge server:**
   ```
   cd test-server
   curl -LO "https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.1-<version>/forge-1.20.1-<version>-installer.jar"
   java -jar forge-1.20.1-<version>-installer.jar --installServer
   echo "eula=true" > eula.txt
   echo "-Xmx2G" > user_jvm_args.txt
   ```

2. **Download mods into `test-server/mods/`:** Create, KubeJS, Rhino, Architectury, Flywheel, Ponder, and the built `createmorefans` jar. All are available from Maven (see `build.gradle` for repository URLs). Create version strings on maven use the format `6.0.X-BUILD` (e.g., `6.0.8-289`), not `6.0.X` directly. In a production server use the full Create jar (not `:slim`), which bundles Ponder, Flywheel and Registrate.

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
- When testing across Create versions, check the `META-INF/mods.toml` inside the Create jar for its minimum Forge version (`versionRange`) and Ponder version requirement.

## Key Dependencies

- Forge, Create (6.0.x), KubeJS (2001.x), JEI — versions in `gradle.properties`
- All version properties and mod metadata are centralized in `gradle.properties`
