# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] - 2026-10-04

### Added

- Builds for Minecraft 1.20.1 / Forge are now published alongside the 1.21.1 / NeoForge builds.

### Changed

- The jar file name now includes the Minecraft version (`createmorefans-<minecraft version>-<mod version>.jar`).

### Fixed

- Crash with `NoSuchMethodError` on `RecipeApplier.applyRecipeOn` when running Create 6.0.7 or newer ([#1](https://github.com/mcm/createmorefans/issues/1)).
- The KubeJS builder methods were unavailable on dedicated servers because they referenced client-only classes.

## [0.1.0] - 2025-08-15

Initial release