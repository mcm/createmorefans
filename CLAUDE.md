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

## Key Dependencies

- NeoForge, Create (6.0.x), KubeJS, JEI — versions in `gradle.properties`
- All version properties and mod metadata are centralized in `gradle.properties`
