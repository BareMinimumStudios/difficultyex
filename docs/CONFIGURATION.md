# Configuration

[Documentation](README.md) · [Installation](INSTALLATION.md) · [Commands](COMMANDS.md) · [Migration](MIGRATION.md)

DifficultyEx stores a flat JSON5 file at `config/difficultyex/config.json5`. Gameplay settings belong to the server. The seven nameplate options are client-local preferences.

## Make changes

For file edits, stop the game or server, edit the existing generated file, then restart. Do not assume vanilla `/reload` reloads this file. The optional in-game Fzzy Config editor opens with `/configure difficultyex`; select **Changes > Apply Changes** before closing. **Done alone discards unapplied edits.**

Run `/difficultyex validate` after changing rules. It reports mistakes without rewriting settings. Its formula check covers syntax, not every possible input result. It checks the server copy of nameplate rules, not each client's preferences.

## Progression

| Key | Default | Meaning |
| --- | --- | --- |
| `startingLevel` | `1` | Global minimum and fallback when no eligible player contributes |
| `maximumLevel` | `1000000` | Global cap |
| `playerRadius` | `100` | Three-dimensional player radius, 0 to 4096 blocks |
| `playerLevelFormula` | `"x"` | Formula applied to each nearby player's PlayerEx level |
| `averageDecrement` | `3` | Maximum random reduction after averaging |
| `averageIncrement` | `3` | Maximum random increase after averaging |

PlayerEx progression levels are separate from vanilla XP levels. Spectators do not contribute. Players exactly on the radius are included. Formula results are averaged before random variation and area restrictions. Set both variation settings to zero for deterministic area levels. New mobs receive a level once; saved mobs retain theirs.

## Mob stats

| Key | Default | Meaning |
| --- | --- | --- |
| `healthPerLevel` | `0.08` | Maximum-health increase per level |
| `armorPerLevel` | `0.08` | Armor increase per level |
| `damagePerLevel` | `0.1` | Attack-attribute increase per level |
| `experiencePerLevel` | `0.1` | Base XP reward increase per level |
| `mobBlacklist` | `[]` | Entity IDs or patterns excluded from initial level assignment |

Positive rates scale from the base attribute using the mob level: level 20 with health rate 0.08 has a nominal health multiplier of `1 + 20 × 0.08 = 2.6`. Vanilla and other mods' attribute limits still apply. Non-positive health, armor and damage rates disable that boost. Non-finite rates are guarded. XP still follows vanilla kill eligibility and enchantment adjustments.

## Area and entity rules

| Scope | Minimum map | Maximum map | Key format |
| --- | --- | --- | --- |
| Entity | `entityStartingLevels` | `entityMaximumLevels` | Entity ID or case-insensitive whole-ID regex |
| Dimension | `dimensionStartingLevels` | `dimensionMaximumLevels` | Exact dimension ID |
| Biome | `biomeStartingLevels` | `biomeMaximumLevels` | Exact biome ID |
| Structure | `structureStartingLevels` | `structureMaximumLevels` | Exact structure ID |

All eight maps default to `{}`. Values must be positive integers. Remove an entry to disable its rule; zero is not a disable switch. The highest matching minimum and lowest matching maximum apply. Maximum restrictions win when bounds conflict, and the global maximum remains a cap.

Merge this fragment into the generated file:

```json5
dimensionStartingLevels: { "minecraft:the_nether": 20 },
dimensionMaximumLevels: { "minecraft:the_nether": 60 },
biomeStartingLevels: { "minecraft:desert": 10 },
structureStartingLevels: { "minecraft:ancient_city": 40 },
entityMaximumLevels: { "minecraft:cow": 5 },
```

These IDs illustrate configuration, not acceptance testing of every listed structure. Modded registry IDs work when the corresponding content is installed. An exact-match regex must escape literal dots where relevant; JSON strings also require backslashes to be escaped.

### Structures and loading

`structureRadius` defaults to 50 blocks and accepts 0 to 128. Influence uses the inclusive horizontal X/Z bounding rectangle expanded by that radius, including corners. Vertical position does not change this influence.

Only loaded reference chunks and loaded structure-origin chunks are checked. DifficultyEx does not force-load terrain to resolve a rule. If the origin is absent, that structure rule is skipped for the new mob. Loading it later affects later spawns, not an existing mob's assigned level.

## Client nameplates

| Key | Default | Meaning |
| --- | --- | --- |
| `nameplatesEnabled` | `true` | Master display toggle |
| `nameplateDistance` | `20` | Display distance, 0 to 64 blocks |
| `nameplateHostileOnly` | `false` | Restrict labels to hostile mobs |
| `nameplateBlacklist` | `[]` | Entity IDs or patterns hidden from nameplates |
| `nameplateShowLevel` | `true` | Level text |
| `nameplateShowHealth` | `true` | Ten-segment health bar |
| `nameplateShowHealthText` | `true` | Current and maximum HP text |

Disabling every label element does not force a nameplate. Vanilla team visibility, viewer-relative invisibility and the global render-names setting are respected. Singleplayer rendering and live master-toggle behavior passed on NeoForge; other visual edge cases and actual two-client independence remain in the [QA checklist](QA_CHECKLIST.md).
