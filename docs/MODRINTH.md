![Difficulty EX](https://raw.githubusercontent.com/BareMinimumStudios/difficultyex/1.21.1/docs/assets/difficultyex-banner.png)

DifficultyEx brings configurable RPG-style mob levels to Minecraft. New mobs are assigned a level based on nearby players' **PlayerEx levels**. Set a gentler starting biome, a tougher Nether, or a dungeon with its own level range.

## Features

- **Progression**: Set the player radius, level formula, and random variation. Mobs keep their assigned levels through saves and restarts.
- **Area and mob rules**: Set minimum and maximum levels by dimension, biome, loaded structure, or mob type. Exclude selected mobs from initial scaling.
- **Combat and rewards**: Tune maximum health, armor, melee attack damage, and base XP rewards independently.
- **Optional nameplates**: Show mob level, a health bar, and current/max HP. Each player can adjust range, show only hostile mobs, or hide specific mob types.

## Install

**DifficultyEx 1.1.0** supports **Minecraft 1.21.1** on **Fabric and NeoForge** with **Java 21**. Install matching builds and dependencies on the server and clients.

Both builds use PlayerEx, Data Attributes, and Fzzy Config. The tested PlayerEx build includes Remnant. Fabric also needs Fabric Loader 0.20+, Fabric API, and Fabric Language Kotlin. NeoForge needs the complete Kotlin for Forge distribution.

[Modrinth versions](https://modrinth.com/mod/qdJ4GLvL/versions) · [Installation and tested dependency versions](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/INSTALLATION.md)

## Configure

Edit `config/difficultyex/config.json5` while the game or server is stopped, then restart. You can also use Fzzy Config's in-game editor with `/configure difficultyex`. Select **Changes > Apply Changes** to save. The server controls mob scaling, and each client controls its nameplate preferences.

[Configuration reference and examples](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/CONFIGURATION.md)

## Operator commands

- `/difficultyex validate` checks the active configuration.
- `/difficultyex inspect <target>` shows a mob's level and stats.
- `/difficultyex set <target> <level>` changes a mob's level and preserves its health percentage.

[Permissions and command examples](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/COMMANDS.md)

[Documentation](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/README.md) · [Source](https://github.com/BareMinimumStudios/difficultyex/tree/1.21.1) · [Issues](https://github.com/BareMinimumStudios/difficultyex/issues) · [Changelog](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/CHANGELOG.md) · [CurseForge](https://www.curseforge.com/minecraft/mc-mods/difficultyex) · [Discord](https://discord.gg/pcRw79hwey)

[License](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/LICENSE) · [Inherited MIT notice](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/ORIGINAL_MIT_LICENSE.txt)
