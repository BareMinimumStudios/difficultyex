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

## Low-health operator level changes (runtime acceptance pending)

The setter preserves current health as a fraction of the old maximum. Three unit tests cover 0.5/40 becoming 0.25/20, zero remaining zero, and finite/clamped results for invalid or extreme inputs. In a disposable world, reduce a test mob below 1 HP and lower its level; verify its health follows the same percentage instead of rising to 1 HP. The previous 18/36 to 26/52 runtime scenario remains covered by the shared helper test. The complete suite now passes 56 tests; these additional low-health command cases have not been exercised in a game.

## Level-map value validation (runtime acceptance pending)

Both loaders configure positive integer validation for entity, dimension, biome, and structure minimum/maximum maps. In a disposable config, verify that valid levels survive load/save and that zero or negative values report validation errors and cannot persist as valid entries. Remove an entry to disable its rule. Verify the optional editor explains key formats and conflicting maximum priority. A map value above the global maximum remains limited by that global maximum when a new mob spawns. The current check is compilation plus the existing 53-test suite, not GUI acceptance of these new bounds.

## Legacy configuration import (NeoForge startup-tested 2026-10-08)

The 1.20.1 owo-lib file was named `config/difficultyex-config.json5` (the `.json` variant is also supported). At startup, if `config/difficultyex/config.json5` is absent, DifficultyEx creates it by mapping recognized legacy options from `dimensionSettings`, `biomeScalingSettings`, `structureScalingSettings`, `scalingLevelSettings`, and `visualSettings` into modern Fzzy Config fields. **Neither an existing modern config nor the original legacy file is overwritten.** All unrelated files are unaffected. An unreadable old config is reported in the log and is left in place. Seven migration tests also verify filtering malformed blacklist and level-map entries while preserving valid rules, and keeping modern defaults for invalid numeric settings. Fractional or overflowing integers and non-finite rates are skipped with a warning. These malformed-input cases were tested with synthetic JSON5, not a production file.

The retired nameplate offset, scale, and color options are not migrated because the replacement nameplate renderer does not yet expose matching controls. The old per-player difficulty selection enum is also not carried over. Confirm the new settings visually in the Fzzy Config menu; do not delete the original config until satisfied. This migration was tested with synthetic old JSON5 in unit tests and in a real NeoForge dedicated-server startup, but still needs validation against a real 1.20.1 production configuration.

## Synced entity-data metadata (NeoForge codec-tested 2026-10-08)

A disposable operator-only runtime probe exercised the **actual** Minecraft `ClientboundSetEntityDataPacket.STREAM_CODEC` rather than merely inspecting the server's stored level. A newly spawned zombie's level was set to **12**; its non-default metadata was encoded into a `RegistryFriendlyByteBuf`, decoded, and applied with `SynchedEntityData.assignValues` to a separate entity instance. That entity reported **level 12**. A subsequent dirty update changed the original zombie to **level 27**, and the receiver updated to **27** after the same packet round trip. The probe did not involve a connected client; real multiplayer synchronization remains a separate acceptance check.

The temporary test command and RCON helper were removed before building, the test mob discarded, the force-load ticket removed, and RCON disabled. Verify live server-to-client health/max-health/level updates and world-reload persistence with two authenticated clients before release.

## Nameplate translations (NeoForge client startup-checked 2026-10-08)

The prior nameplate `en_us.json` contained incorrectly encoded legacy color codes and an unsupported Java-style `%d/%d` health format. Minecraft 1.21.1 `TranslatableContents` accepts `%s`, not `%d`; an invalid format triggers literal-text fallback instead of substituting current/max health. The updated strings use `Lv.%s` and `HP: %s/%s` with component-based yellow/gray styles. Three regression tests parse the **packaged UTF-8 language resource**, verify supported placeholders, and reject legacy formatting codes. NeoForge client startup and resource loading passed after the correction; the test client was closed normally. The later cow check verified translated level and HP text. Low health and independent display toggles still require acceptance testing.

## Nameplate visibility (NeoForge in-world cow nameplate checked 2026-10-09)

In a disposable test world with a client connected, spawn an ordinary zombie and set it to level 20 using `/difficultyex set @e[type=minecraft:zombie,sort=nearest,limit=1] 20`. Confirm the level, name, ten-segment health bar, and health number render at the configured distance; check all three label/bar toggles separately and at zero, and use an occluding wall and an invisibility effect. Apply GUI edits through **Changes → Apply Changes** and check their live effect; restart to verify persistence.

To test vanilla team restrictions, tag a single test mob `difficultyex_team_test`, then run:

```mcfunction
team add difficultyex_hidden
team modify difficultyex_hidden nametagVisibility never
team join difficultyex_hidden @e[tag=difficultyex_team_test,limit=1]
```

The modded nameplate should remain hidden. Test the `hideForOtherTeams` and `hideForOwnTeam` values using two player teams as well. A unit-tested nameplate formatting change prevents very low-health living mobs showing zero segments, and shows at most nine segments when health is below maximum. Check the label at full, 50%, and nearly zero health. Blacklist regex matching is cached and safe for malformed expressions; test changing the blacklist via the config GUI. The code's team policy is unit-tested. The basic NeoForge in-world render passed on a creative test cow at levels 1 and 20, with yellow level text, a green ten-segment bar and correctly translated HP text. Live enabling/disabling of the entire nameplate was also visually verified through the Fzzy GUI. Team policies, occlusion, health-text-only/level-only variants and multi-client acceptance remain outstanding. Clean up via `/team remove difficultyex_hidden` and remove any tagged test mobs.

## Fzzy Config client-local sync contract (bytecode-tested on both loaders 2026-10-09)

Three automated tests now inspect actual `DifficultyExConfig.class` files for Fabric and NeoForge after compiling both loaders. They require all **seven** nameplate preferences (`nameplatesEnabled`, `nameplateDistance`, `nameplateHostileOnly`, `nameplateBlacklist`, `nameplateShowLevel`, `nameplateShowHealth`, `nameplateShowHealthText`) to have Fzzy Config's runtime-visible `@NonSync` field annotation, verify progression/world/entity/structure/combat fields are not annotated, and ensure both loader classes expose the same config field names and JVM types. `gradlew test` now compiles both loader configs before executing these checks. Both variants passed.

This guards the declared sync contract, but **does not simulate a server pushing settings to two different client accounts**. NeoForge singleplayer GUI and basic in-world cow nameplates passed visual acceptance; authenticated two-client synchronization remains untested. The separate-player preference test below remains mandatory before release.

## Fzzy Config GUI and multiplayer settings (NeoForge singleplayer GUI tested 2026-10-09)

The JSON5 configuration is the primary interface; DifficultyEx ships no custom NeoForge Mods-button screen bridge. Fzzy Config's existing optional editor was opened and exercised with `/configure difficultyex` in a live NeoForge integrated world. The nameplate Boolean control, numeric distance slider and map/list editor buttons are visible; the Boolean was actually edited and persisted. **Important:** after editing, click **Changes → Apply Changes**; clicking only **Done** silently discards pending edits. This was verified by first observing a discarded change and then confirming the applied setting in `run/config/difficultyex/config.json5` and in-world rendering. As the settings live in one mixed server/client config, all controls, including `@NonSync` nameplate fields, display **Not in Game** at the title screen. Join a world to edit them. Other controls and multiplayer GUI sync remain to be verified.

Test a `minecraft:zombie` entry in `entityStartingLevels`, e.g. 25, and spawn a new zombie to verify minimum level 25. This map rule already passed in a NeoForge dedicated-server smoke test with an existing JSON5 config (25/60HP/6 armor); manual GUI edits are not yet verified.

With a dedicated server and two clients, set different **Nameplates** options on each client. Ensure saving locally doesn't change the other client's preferences or change the server's nameplate config. Confirm a client reconnect retains its choices, while operator-edited progression rules synchronize from the server; existing mob levels should stay persisted rather than rerolling.

## PlayerEx-driven mob levels (NeoForge fake-player integration-tested 2026-10-08)

The controlled NeoForge test used eight transient fake-player scenarios via the **real** PlayerEx 5.0.1 `PlayerStateService` and normal zombie spawn initialization. With random variation zero, the test confirmed starting-level fallback 7; one nearby PlayerEx level-23 player yielding 23; two nearby PlayerEx levels 10/30 yielding 20; faraway level-100 player excluded; `playerLevelFormula = "x*2"` yielding 40 for levels 10/30; radius-10 inclusivity (player exactly 10 blocks away yielded 19, at 10.0001 blocks yielded fallback 7); and a player 11 blocks vertically away yielding fallback 7. The staged code additionally excludes spectators from level calculations (unit-tested). The probe, its fake-player list entries, and test zombies were removed.

**Still perform a real multiplayer acceptance check** before release: use two authenticated test accounts with distinct PlayerEx progression levels, disable random level deviation temporarily, set a small `playerRadius`, and summon separately tagged mobs within range of one, both, and neither player. Check `/difficultyex inspect @e[tag=<unique_test_tag>,limit=1]` after each spawn. Confirm expected per-player formula/average, spectator exclusion, 3D distance and health/attribute scaling; verify saved mobs don't reroll after a player moves or logs out. Restore the original config and remove test mobs afterward. These in-world multiplayer checks have **not** been run.

## Combat damage and armor (NeoForge live-tested 2026-10-08)

A temporary QA probe ran the real Minecraft combat pipeline in the isolated NeoForge development world. With the standard rates `damagePerLevel=0.1` and `armorPerLevel=0.08`, a level-1 zombie had **3.3 attack** and dealt **3.3 actual damage** to a cow; a level-20 zombie had **9 attack** and dealt **9 actual damage**. Against the same incoming normal mob attack of **5 points**, a level-1 zombie had **2.16 armor** and received **4.92** damage; a level-20 zombie had **5.2 armor** and received **4.50** damage. The command used `Zombie.doHurtTarget` / `LivingEntity.hurt` rather than calling the helper directly. All temporary test entities were discarded, two force-loaded chunks released, server stopped, and RCON reset. The disposable probe is not included in shipped source/JARs.

Five `DifficultyAttributeMath` tests cover normal health/armor/damage multipliers, zero/negative rates, rejection of non-finite rates, and saturation on large multipliers. Vanilla's final armor attributes and damage rules still apply. **Acceptance remaining:** combat involving an authenticated player with equipment/armor, enchantments, difficulty settings, and other modded damage or defense effects.

## XP scaling (NeoForge fake-player live-tested 2026-10-08)

Minecraft 1.21.1 permits animal subclasses to override `getBaseExperienceReward` without calling `Mob`. DifficultyEx intercepts the base XP argument in `LivingEntity.getExperienceReward`, **before** enchantment adjustments, for every `Mob`; non-mobs are unaffected.

A disposable NeoForge runtime probe attributed actual mob deaths to a NeoForge fake player. Under the default `experiencePerLevel=0.1`, a level-1 zombie dropped 5 XP and a level-20 zombie dropped 15 XP. A level-100 cow dropped 11 XP (a random vanilla animal base roll of 1, scaled by 11); a total of 9 XP orbs carried 31 XP. Animal base XP is random, so the expected amount can vary between 11, 22 and 33 for the same level-100 cow. The probe was **deleted** and is not a public command. Ordinary player combat, Looting/enchantment interactions and XP pickup still need acceptance testing.

## Still requiring acceptance testing

- Fabric server/client startup after a compatible public PlayerEx release fixes its 1.21.1 mixin descriptor
- Actual multiplayer mob-level synchronization and client nameplate rendering, including health display toggles
- Additional structure variants, many-mob performance, and real-player movement near structures whose origin chunks load late (exact boundary and unloaded-origin regression tests passed on NeoForge)
- XP-orb collection by a **human-controlled** player and real player-combat/enchantment interactions (NeoForge fake-player XP rewards and zombie-versus-cow attack/armor damage passed the runtime tests)
- Production-origin owo-lib configuration migration (synthetic-file unit test and NeoForge startup smoke test passed); optional mod integrations
- Performance with many mobs and structure rules enabled

## Extreme player-level formula results (automated 2026-10-09)

Three regression tests cover finite values whose double sum overflows: large positive and negative values must cancel in either order, small remaining contributions must retain normal integer rounding, and truly huge averages must saturate to integer limits. Non-finite individual values are still ignored; the configured starting level remains the fallback when no valid values exist. The full IDEA test/build run passed 50 tests with zero failures, errors, or skips. This does not replace authenticated multiplayer acceptance.
