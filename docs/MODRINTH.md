![Difficulty EX](https://raw.githubusercontent.com/BareMinimumStudios/difficultyex/1.21.1/docs/assets/difficultyex-banner.png)

# Difficulty EX

**Minecraft 1.21.1 · Fabric + NeoForge · Java 21**

Give your world MMORPG-style difficulty that grows with player progression. DifficultyEx uses nearby players' PlayerEx levels to assign mob levels, then scales health, armor, attack damage and XP rewards.

Create a gentler starting biome, a dangerous Nether or dungeons with their own level ranges. Optional nameplates show mob levels, a health bar and current HP.

**1.21.1 development preview:** packaged server startup, commands and save/restart checks pass on both loaders. Authenticated multiplayer, visual edge cases and broader gameplay acceptance remain pending. Older 1.20.1 files use different configuration and integrations. Choose the correct loader and Minecraft version.

## Make it your world

- Set minimum and maximum levels by dimension, biome, structure or entity type.
- Tune the nearby-player formula, radius and random level variation.
- Adjust health, armor, attack damage and XP rates.
- Exclude mobs from scaling or hide selected nameplates.
- Choose level text, health bar, HP text, range and hostile-only display.
- Keep assigned mob levels through save and restart.

Structure rules inspect loaded chunks and loaded origins only. They do not force-load terrain, and later origin loading does not reroll existing mobs.

## Installation

Install matching builds on the server and clients. Fabric requires Loader 0.20 or newer; this branch is tested with 0.20.1. Both loaders need PlayerEx 5.0.1, Data Attributes 3.0.0 and Fzzy Config 0.7.7 for the loader. The pinned PlayerEx supplies Remnant 3.0.0. Fabric also needs Fabric API and Fabric Language Kotlin; NeoForge needs the complete Kotlin for Forge distribution. Java 21 is required.

[Installation guide and tested versions](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/INSTALLATION.md)

## Configuration

Settings are stored in `config/difficultyex/config.json5`. Edit while stopped, then restart, or use the optional `/configure difficultyex` editor. Choose **Changes > Apply Changes** to save. Server gameplay rules and client nameplate choices are separate. owo-lib is no longer required by this port.

[Configuration reference and examples](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/CONFIGURATION.md)

## Operator tools

All administration commands require permission level 2.

```text
/difficultyex validate
/difficultyex inspect <target>
/difficultyex set <target> <level>
```

Validate active rules, inspect a mob's stats, or change its level while preserving its health percentage. These commands do not change player progression.

[Commands and examples](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/COMMANDS.md)

## Updating from 1.20.1

Supported legacy config fields import when the modern config is absent; the original file remains untouched. Supported saved mob-level compounds also import. Back up first. GeckoLib-specific nameplates, Traveler's Titles and old visual styling controls are not part of the current port.

[Migration guide](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/MIGRATION.md) · [Compatibility](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/COMPATIBILITY.md)

## Find everything

- [Documentation hub](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/README.md)
- [Source](https://github.com/BareMinimumStudios/difficultyex/tree/1.21.1) · [Issues](https://github.com/BareMinimumStudios/difficultyex/issues)
- [Port status](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/PORT_STATUS.md) · [Changelog](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/CHANGELOG.md)
- [Modrinth](https://modrinth.com/mod/qdJ4GLvL) · [CurseForge](https://www.curseforge.com/minecraft/mc-mods/difficultyex)
- [Bare Minimum Studios](https://github.com/BareMinimumStudios) · [Community Discord](https://discord.gg/pcRw79hwey)
- [License](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/LICENSE) · [Inherited MIT notice](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/ORIGINAL_MIT_LICENSE.txt)
