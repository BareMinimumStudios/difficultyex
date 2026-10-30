# DifficultyEx 1.1.0 for Minecraft 1.21.1

Fabric and NeoForge port. Development release; multiplayer and visual acceptance remain incomplete.

## Included

- PlayerEx-based mob levels with entity, dimension, biome and loaded-structure bounds.
- Configurable health, armor, melee attack attributes and XP scaling.
- Persistent mob levels and saved health, with supported prior mob-level imports.
- Fzzy Config JSON5 settings, optional editor and seven client-local nameplate preferences.
- Non-destructive import of supported 1.20.1 configuration fields.
- Operator commands for config validation, mob inspection and level changes.
- Shared level and health nameplates that respect team and invisibility policies.

## Requirements

Minecraft 1.21.1 and Java 21. Fabric requires Loader 0.20 or newer; 0.20.1 was tested. Install the matching PlayerEx 5.0.1, Data Attributes 3.0.0, Fzzy Config 0.7.7 and loader Kotlin runtime. The pinned PlayerEx distribution supplies Remnant 3.0.0. Fabric also requires Fabric API. See the [installation guide](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/INSTALLATION.md) for exact tested artifacts.

## Validation and limitations

Both packaged dedicated servers passed commands, persistence and offline config-edit restarts. Scripted combat, equipment, XP, migration, structures and synchronization serialization checks passed. These do not replace two authenticated clients, reconnect testing, human gameplay, client visuals or populated-world performance acceptance.

GeckoLib render hooks, Traveler's Titles labels and old nameplate color, scale and offset controls are not ported. This is not complete 1.20.1 feature parity. Back up worlds and configuration before evaluating the port.
## Guides and support

- [Configuration and area rules](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/CONFIGURATION.md)
- [Operator commands](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/COMMANDS.md)
- [Migration from 1.20.1](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/MIGRATION.md)
- [Compatibility and known limitations](https://github.com/BareMinimumStudios/difficultyex/blob/1.21.1/docs/COMPATIBILITY.md)
- [Report an issue](https://github.com/BareMinimumStudios/difficultyex/issues)
