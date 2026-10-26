# Upgrading from 1.20.1

[Documentation](README.md) · [Configuration](CONFIGURATION.md) · [Compatibility](COMPATIBILITY.md)

Back up worlds and configs before changing Minecraft or mod versions. Supported DifficultyEx imports do not establish that an entire modpack world can safely upgrade.

## Configuration

On first startup, DifficultyEx checks for `config/difficultyex-config.json5`, or the `.json` alternative, only if the modern `config/difficultyex/config.json5` is absent. Supported fields are imported before the new configuration loads. The original file is preserved. An existing modern file always takes priority.

Progression, stat rates, supported area/entity maps and nameplate toggles are imported. Invalid entries are skipped or corrected by validation. Positive values are required for rule-map levels; zero is not a way to disable a rule.

The old visual offset, scale and color controls are not imported. Legacy per-player difficulty selection is not part of the new scaling behavior. Do not copy old nested JSON5 sections directly into the new flat schema.

## Saved mobs

Supported prior Cardinal Components compound data is read for a saved mob level and written back using the modern `difficultyex_level` tag. Existing modern data takes precedence over legacy data, including zero or malformed modern tags. This is not support for every ancient save format.

## Verified scope

A production 1.20.1 config copy preserved the tested dimension, biome and blacklist entries without changing the original. Both modern persistence and supported legacy mob-level save/restart checks passed. See [production config results](BACKGROUND_QA_2026-10-09.md), [saved-mob results](COMMAND_AND_LEGACY_QA_2026-10-09.md) and [the parity audit](PARITY_AUDIT_1.20.1.md).
