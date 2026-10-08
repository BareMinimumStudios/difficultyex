# DifficultyEx

[![Build](https://github.com/BareMinimumStudios/difficultyex/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/BareMinimumStudios/difficultyex/actions/workflows/build.yml)
[![License: BML](https://img.shields.io/badge/license-BML--1.0-lightgrey)](https://github.com/BareMinimumStudios/bare-minimum-license)
[![Minecraft 1.21.1](https://img.shields.io/badge/Minecraft-1.21.1-62b24a)](https://minecraft.net/)

**DifficultyEx** brings RPG-style enemy progression into Minecraft. Mobs inherit levels from nearby players, with configurable limits for each dimension, biome, and entity type. Higher-level enemies have tougher combat attributes and award more experience.

Maintained by [Bare Minimum Studios](https://github.com/BareMinimumStudios/difficultyex). The original project and its authors remain credited in repository history.

## Port status

The Minecraft 1.21.1 rewrite is **under development**, not a published release. The core progression system has been migrated to shared Fabric and NeoForge sources. Client nameplate rendering, structure-based rules, optional integrations, and full compatibility testing are still pending. Do not deploy this development build to an important world.

## Compatibility

| Loader | Minecraft | Language | Configuration |
| --- | --- | --- | --- |
| Fabric | 1.21.1 | Fabric Language Kotlin | Fzzy Config |
| NeoForge | 1.21.1 | Kotlin for Forge | Fzzy Config |

DifficultyEx builds against [PlayerEx](https://github.com/BareMinimumStudios/playerex) 5.0.1, [Data Attributes](https://github.com/BareMinimumStudios/data-attributes) 3.0.0, and [Remnant](https://github.com/BareMinimumStudios/remnant) 3.0.0, using the **public 1.21.1 release artifacts**, not whatever happens to be installed on development servers. Both loaders require their appropriate Kotlin language mod and Fzzy Config. Fabric also requires Fabric API. **owo-lib is no longer required.**

The dependency versions are pinned in [libraries.toml](libraries.toml).

## Build

Install Java 21, then run:

```powershell
.\gradlew.bat clean build
```

On Linux/macOS use `./gradlew clean build`. The wrapper uses **Gradle 9.8.0**, [Cloche](https://github.com/terrarium-earth/Cloche) for multi-loader builds, and the MixinMCP Gradle decompile plugin. Builds produce loader-specific JAR files in `build/libs/`. Do not use a development JAR on a production server.

## Scaling rules

A new mob's level is derived from nearby players' **PlayerEx progression levels**, not their vanilla XP levels. If no player is nearby, the configured starting level is used. Random level variation and minimum/maximum restrictions can be configured independently. Existing mobs keep their saved levels on reload.

Scaling currently affects maximum health, armor, attack damage, and experience. Damage and health use attribute modifiers rather than modifying raw damage hooks. Server-side Fzzy Config settings are maintained separately for Fabric and NeoForge.

## Testing

Use temporary test worlds and copies of the server launch environments. The supplied Fabric and NeoForge development servers are reference environments only; installed mod versions are **not** the dependency source of truth. Validate spawning, NBT persistence, entity synchronization, progression changes, damage, XP rewards, and dedicated-server startup on both loaders before shipping.

## License

See [LICENSE](LICENSE) and [Bare Minimum License](https://github.com/BareMinimumStudios/bare-minimum-license).
