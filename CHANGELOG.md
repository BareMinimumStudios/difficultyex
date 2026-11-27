# Changelog

All notable changes to DifficultyEx are documented here. This project follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- Updated the project license to The Bare Minimum License v1.1.

## [1.1.1] - 2026-11-27

### Added

- GitHub Releases now include both loader JARs under one version.

### Changed

- Mob level assignment avoids temporary player lists when evaluating nearby PlayerEx levels.
- Nameplate rendering skips mob ID lookups when the nameplate blacklist is empty.
- Build and publish workflows generate release notes from this changelog.

## [1.1.0] - 2026-11-03

### Added

- Added Fabric and NeoForge builds for Minecraft 1.21.1 with Java 21.
- Added persistent, synchronized mob levels and configurable stat and XP scaling. Set level bounds by entity, dimension, biome, or loaded structure.
- Added operator commands to validate settings, inspect mobs, and change their levels.
- Restored level and health nameplates with team visibility, invisibility checks, and client preferences.
- Added support for importing prior settings and saved mob levels without overwriting the originals.
- Added cross-loader tests, manual publishing, and release bundle verification.

### Changed

- Replaced owo-lib with Fzzy Config for JSON5 settings and optional in-game editing.
- Refreshed branding and release documentation.
- Adopted BML 1.0 and retained the inherited MIT notice in both JARs.

### Fixed

- Fixed health clamping, level changes for injured or dead mobs, passive mob XP hooks, and numeric overflow.
- Fixed Fabric development launches and IDE resolution for shared mixins.

## Earlier changes

### Fixed

- Fixed Crunch missing from the packaged mod.

[Unreleased]: https://github.com/BareMinimumStudios/difficultyex/compare/1.1.1%2B1.21.1...1.21.1
[1.1.1]: https://github.com/BareMinimumStudios/difficultyex/releases/tag/1.1.1%2B1.21.1
[1.1.0]: https://www.curseforge.com/minecraft/mc-mods/difficultyex/files/all
