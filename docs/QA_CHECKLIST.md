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

## Exact structure-radius edges (NeoForge live-tested 2026-10-08)

The generated `minecraft:village_taiga` previously located at X=0, Z=608 had a runtime structure bounding box of **X=−87..53, Z=530..685**. With `structureRadius=16`, `structureStartingLevels={"minecraft:village_taiga":30}`, and both average level variations set to zero, zombies at X=−103 and 69 (Z=600) were level 30; at X=−104 and 70 they were level 1. Similarly, Z=514 and 701 (X=0) were level 30, while Z=513 and 702 were level 1. The corner X=−103, Z=514 gained level 30 and X=−104, Z=514 stayed level 1. This confirms an inclusive X/Z rectangle expanded by the radius, even at a corner. Coordinates depend on the development world's seed and structure; do not copy them to production.

The temporary NeoForge bounding-box inspection command was removed before packaging. The test ran only on the ignored development world, used tagged invulnerable/no-gravity zombies, and cleaned up all ten mobs and force-load tickets. The original dev config was restored byte-for-byte and RCON disabled. Other structure types remain to be checked.

## Unloaded structure-origin chunks (NeoForge live-tested 2026-10-08)

Vanilla `StructureManager.startsForStructure` can load a structure's *origin chunk* while resolving a reference in an already loaded chunk. DifficultyEx now uses `ServerChunkCache.getChunkNow` for both the reference and the origin; it must not force-load or generate missing chunks when spawning mobs.

The isolated taiga village has its origin at chunk `(0,38)`. With a radius of 16 and `minecraft:village_taiga` configured to start at level 30, two reference/boundary chunks `(-7,37)` and `(-6,37)` were loaded. A temporary diagnostic confirmed `(-6,37)` held one village reference, but `(0,38)` was **not loaded**. A zombie spawned at `(-103,150,600)` stayed at **level 1**, and the origin chunk remained absent. Once the origin `(0,38)` was explicitly loaded, another zombie at the same location spawned at **level 30**. The earlier zombie stayed level 1 (spawn-once persistence). **This is an intentional conservative tradeoff:** rules requiring an unloaded structure origin do not apply until that origin loads. Validate the behavior with actual player movement before release.

The development-only diagnostic was deleted, two mobs were killed, the three force-load tickets removed, and the original dev config restored. Neither RCON nor the diagnostic is enabled in a distributable JAR.

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

## Fzzy Config GUI and multiplayer settings (NeoForge server smoke-tested 2026-10-08)

In a disposable client, open the **DifficultyEx** Fzzy Config screen (via Mod Menu / supported config GUI entry). Check that the **Progression**, **Mob rules**, **World rules**, **Structure rules**, and **Nameplates** sections present editable sliders/text boxes, boolean toggles, list/map editors. Validate that saved `config/difficultyex/config.json5` retains its original field names and doesn't lose custom values.

Test a `minecraft:zombie` entry in `entityStartingLevels`, e.g. 25, and spawn a new zombie to verify minimum level 25. This map rule already passed in a NeoForge dedicated-server smoke test with an existing JSON5 config (25/60HP/6 armor); manual GUI edits are not yet verified.

With a dedicated server and two clients, set different **Nameplates** options on each client. Ensure saving locally doesn't change the other client's preferences or change the server's nameplate config. Confirm a client reconnect retains its choices, while operator-edited progression rules synchronize from the server; existing mob levels should stay persisted rather than rerolling.

## XP scaling (NeoForge fake-player live-tested 2026-10-08)

Minecraft 1.21.1 permits animal subclasses to override `getBaseExperienceReward` without calling `Mob`. DifficultyEx intercepts the base XP argument in `LivingEntity.getExperienceReward`, **before** enchantment adjustments, for every `Mob`; non-mobs are unaffected.

A disposable NeoForge runtime probe attributed actual mob deaths to a NeoForge fake player. Under the default `experiencePerLevel=0.1`, a level-1 zombie dropped 5 XP and a level-20 zombie dropped 15 XP. A level-100 cow dropped 11 XP (a random vanilla animal base roll of 1, scaled by 11); a total of 9 XP orbs carried 31 XP. Animal base XP is random, so the expected amount can vary between 11, 22 and 33 for the same level-100 cow. The probe was **deleted** and is not a public command. Ordinary player combat, Looting/enchantment interactions and XP pickup still need acceptance testing.

## Still requiring acceptance testing

- Fabric server/client startup after a compatible public PlayerEx release fixes its 1.21.1 mixin descriptor
- Actual multiplayer mob-level synchronization and client nameplate rendering, including health display toggles
- Additional structure variants, many-mob performance, and real-player movement near structures whose origin chunks load late (exact boundary and unloaded-origin regression tests passed on NeoForge)
- XP-orb collection by a **human-controlled** player and modified damage/armor in combat (NeoForge fake-player-attributed zombie/cow kills and orb totals passed the runtime probe)
- Production-origin owo-lib configuration migration (synthetic-file unit test and NeoForge startup smoke test passed); optional mod integrations
- Performance with many mobs and structure rules enabled
