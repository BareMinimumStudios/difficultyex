# DifficultyEx

[![Build](https://github.com/BareMinimumStudios/difficultyex/actions/workflows/build.yml/badge.svg?branch=1.21.1)](https://github.com/BareMinimumStudios/difficultyex/actions/workflows/build.yml)
[![License: BML](https://img.shields.io/badge/license-BML--1.0-lightgrey)](https://github.com/BareMinimumStudios/bare-minimum-license)
[![Minecraft 1.21.1](https://img.shields.io/badge/Minecraft-1.21.1-62b24a)](https://minecraft.net/)

**DifficultyEx** brings RPG-style enemy progression into Minecraft. Mobs inherit levels from nearby players, with configurable limits for each dimension, biome, structure, and entity type. Higher-level enemies have tougher combat attributes and award more experience.

Maintained by **pokesmells** for [Bare Minimum Studios](https://github.com/BareMinimumStudios/difficultyex). Historical attribution and license notices remain available in the repository history.

## Port status

The Minecraft 1.21.1 rewrite is **under development**, not a published release. The core progression system has been migrated to shared Fabric and NeoForge sources. Client nameplate rendering, structure-based rules, optional integrations, and full compatibility testing are still pending. Do not deploy this development build to an important world.

## Compatibility

| Loader | Minecraft | Language | Configuration |
| --- | --- | --- | --- |
| Fabric | 1.21.1 | Fabric Language Kotlin | Fzzy Config |
| NeoForge | 1.21.1 | Kotlin for Forge | Fzzy Config |

DifficultyEx builds against [PlayerEx](https://github.com/BareMinimumStudios/playerex) 5.0.1, [Data Attributes](https://github.com/BareMinimumStudios/data-attributes) 3.0.0, and [Remnant](https://github.com/BareMinimumStudios/remnant) 3.0.0, using the **public 1.21.1 release artifacts**, not whatever happens to be installed on development servers. Both loaders require their appropriate Kotlin language mod and Fzzy Config. Fabric also requires Fabric API. **owo-lib is no longer required.** On first startup, if `config/difficultyex/config.json5` does not exist but the old owo-lib `config/difficultyex-config.json5` (or `.json`) is present, DifficultyEx imports supported legacy fields automatically. The old file remains untouched. An existing Fzzy Config always takes priority; see the [migration notes](docs/QA_CHECKLIST.md).

The dependency versions are pinned in [libraries.toml](libraries.toml).

## Build

Install Java 21, then run:

```powershell
.\gradlew.bat clean build
```

On Linux/macOS use `./gradlew clean build`. The wrapper uses **Gradle 9.8.0**, [Cloche](https://github.com/terrarium-earth/Cloche) for multi-loader builds, and the MixinMCP Gradle decompile plugin. Builds produce loader-specific JAR files in `build/libs/`. Do not use a development JAR on a production server.

## Scaling rules

A new mob's level is derived from nearby players' **PlayerEx progression levels**, not their vanilla XP levels. If no player is nearby, the configured starting level is used. Random level variation and minimum/maximum restrictions can be configured independently. Existing mobs keep their saved levels and scaled health on reload, including current health above the vanilla maximum.

Scaling currently affects maximum health, armor, attack damage, and experience. Experience multipliers apply to **all mob subclasses**, including animals that compute their own randomized base XP; vanilla player-kill eligibility and enchantment adjustments still apply. Damage and health use attribute modifiers rather than modifying raw damage hooks. The Fzzy Config screen groups editable options into **Progression**, **Mob rules**, **World rules**, **Structure rules**, and **Nameplates**. Combat and level-scaling settings are server-authoritative; nameplate settings are local to each client and do not override other players' preferences. The Fabric and NeoForge configurations use the same JSON5 field names.

**Structure rules:** Configure `structureStartingLevels` and `structureMaximumLevels` with structure IDs such as `minecraft:desert_pyramid`, and `structureRadius` (default 50 blocks). Only already-loaded surrounding chunks are inspected, and lookup radius is capped at 128 blocks to avoid loading terrain or excessive spawn costs. Per-structure restrictions combine with dimension, biome, and mob restrictions. The highest minimum and lowest maximum apply; maximums take priority if configured bounds conflict.

**Mob nameplates:** Level and health displays now use client-side vanilla nametag rendering on both loaders, with a ten-segment health indicator. The Fzzy Config nameplate options independently control the bar, health text, level text, distance, hostile-only display and blacklist. Vanilla team name-tag restrictions and player-relative invisibility are respected; enabling none of the three label/bar elements does not force a nameplate. Fzzy Config calls the update hooks when GUI changes are applied; an in-world live-update check is still required. Nameplate choices are never sent to the server. In-world appearance still needs visual verification.

## Operator commands

Operators (permission level 2 or above) can inspect a mob with `/difficultyex inspect <target>` and set its level with `/difficultyex set <target> <level>`. The setter synchronizes the mob's level, reapplies scaling modifiers, and preserves its health percentage. For example, `/difficultyex inspect @e[type=minecraft:zombie,sort=nearest,limit=1]` inspects the nearest zombie. Commands are intended for testing and administration; they do not change PlayerEx player levels.

## Testing

Use temporary test worlds and copies of the server launch environments. The supplied Fabric and NeoForge development servers are reference environments only; installed mod versions are **not** the dependency source of truth. Validate spawning, NBT persistence, entity synchronization, progression changes, structure rules, damage, XP rewards, client nameplates, and dedicated-server startup on both loaders before shipping. Run `./gradlew test` for the included level-boundary, XP-reward arithmetic, legacy-config migration and nameplate-visibility unit tests.

## License

Copyright © 2025–2026 Bare Minimum Studios. **DifficultyEx is licensed under the [Bare Minimum License (BML) v1.0](LICENSE)**, matching other Bare Minimum Studios projects. The license covers code and assets under different terms; see the [official license repository](https://github.com/BareMinimumStudios/bare-minimum-license). The previous MIT notice has been retained in [docs/ORIGINAL_MIT_LICENSE.txt](docs/ORIGINAL_MIT_LICENSE.txt) for historical portions.
