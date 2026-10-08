# DifficultyEx 1.21.1 QA checklist

Run these checks in a **disposable test world**, with an operator account. Do not run them in production.

## Mob levels, modifiers and save/reload (NeoForge smoke-tested 2026-10-08)

With default mob scaling rates, use the Minecraft command console or operator chat (omit the slash in a dedicated server console):

```mcfunction
summon minecraft:zombie ~ ~ ~ {NoAI:1b,NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,Tags:["difficultyex_test"]}
difficultyex inspect @e[type=minecraft:zombie,tag=difficultyex_test,limit=1]
difficultyex set @e[type=minecraft:zombie,tag=difficultyex_test,limit=1] 10
attribute @e[type=minecraft:zombie,tag=difficultyex_test,limit=1] minecraft:generic.max_health get
data merge entity @e[type=minecraft:zombie,tag=difficultyex_test,limit=1] {Health:18.0f}
difficultyex set @e[type=minecraft:zombie,tag=difficultyex_test,limit=1] 20
difficultyex inspect @e[type=minecraft:zombie,tag=difficultyex_test,limit=1]
save-all
```

At the default 8% health scaling rate, level 10 should have maximum health **36**; level 20 should have maximum health **52**, with current health **26** after moving up from 18/36. Fully stop and restart the server, then run the inspect command again. The same mob must retain **level 20, 26/52 health, and its scaled armor and damage attributes**. This test exposed and confirmed the fix for the vanilla health-clamping-on-load regression.

When finished, clean up with `/kill @e[type=minecraft:zombie,tag=difficultyex_test]`. The invulnerable tag prevents damage from interfering with the results.

## Structure level bounds (NeoForge live-tested 2026-10-08)

In an isolated development world, configure a known village's structure ID in Fzzy Config. For example, assign `minecraft:village_taiga` a level 30 minimum, then use `/locate structure #minecraft:village` to find a generated village. At the returned horizontal location, load the chunk and summon a tagged invulnerable/no-gravity zombie. Check `/difficultyex inspect @e[tag=difficultyex_structure_test,limit=1]`: it should spawn at or above 30. A control zombie far outside all configured villages should **not** inherit this structure minimum.

Add `minecraft:village_taiga` to the structure maximum map with a maximum of 20, **restart**, and summon a new village zombie. When minimum 30 conflicts with maximum 20, the new mob should be level 20. The test world used X=0, Z=608, but coordinates are seed-dependent. Remove tagged mobs and `/forceload remove <x> <z>` for any temporarily forced chunks, then restore development config.

## Legacy configuration import (NeoForge startup-tested 2026-10-08)

The 1.20.1 owo-lib file was named `config/difficultyex-config.json5` (the `.json` variant is also supported). At startup, if `config/difficultyex/config.json5` is absent, DifficultyEx creates it by mapping recognized legacy options from `dimensionSettings`, `biomeScalingSettings`, `structureScalingSettings`, `scalingLevelSettings`, and `visualSettings` into modern Fzzy Config fields. **Neither an existing modern config nor the original legacy file is overwritten.** All unrelated files are unaffected. An unreadable old config is reported in the log and is left in place.

The retired nameplate offset, scale, and color options are not migrated because the replacement nameplate renderer does not yet expose matching controls. The old per-player difficulty selection enum is also not carried over. Confirm the new settings visually in the Fzzy Config menu; do not delete the original config until satisfied. This migration was tested with synthetic old JSON5 in unit tests and in a real NeoForge dedicated-server startup, but still needs validation against a real 1.20.1 production configuration.

## Nameplate visibility (NeoForge startup-checked 2026-10-08)

In a disposable test world with a client connected, spawn an ordinary zombie and set it to level 20 using `/difficultyex set @e[type=minecraft:zombie,sort=nearest,limit=1] 20`. Confirm the level, name, ten-segment health bar, and health number render at the configured distance; check all three label/bar toggles separately and at zero, and use an occluding wall and an invisibility effect. **Restart** between config changes in this development version.

To test vanilla team restrictions, tag a single test mob `difficultyex_team_test`, then run:

```mcfunction
team add difficultyex_hidden
team modify difficultyex_hidden nametagVisibility never
team join difficultyex_hidden @e[tag=difficultyex_team_test,limit=1]
```

The modded nameplate should remain hidden. Test the `hideForOtherTeams` and `hideForOwnTeam` values using two player teams as well. The code's team policy is unit-tested and NeoForge client startup passed, but the actual nameplate appearance still requires this in-world validation. Clean up via `/team remove difficultyex_hidden` and remove any tagged test mobs.

## Still requiring acceptance testing

- Fabric server/client startup after a compatible public PlayerEx release fixes its 1.21.1 mixin descriptor
- Actual multiplayer mob-level synchronization and client nameplate rendering, including health display toggles
- Additional structure variants, exact influence-radius boundaries and many-mob performance with structure rules
- Actual XP-orb drops after a player kills a leveled mob, and modified damage/armor in combat (XP arithmetic has four passing unit tests but the reward hook still needs runtime verification)
- Production-origin owo-lib configuration migration (synthetic-file unit test and NeoForge startup smoke test passed); optional mod integrations
- Performance with many mobs and structure rules enabled
