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

## Still requiring acceptance testing

- Fabric server/client startup after a compatible public PlayerEx release fixes its 1.21.1 mixin descriptor
- Actual multiplayer mob-level synchronization and client nameplate rendering, including health display toggles
- Additional structure variants, exact influence-radius boundaries and many-mob performance with structure rules
- Actual XP-orb drops after a player kills a leveled mob, and modified damage/armor in combat (XP arithmetic has four passing unit tests but the reward hook still needs runtime verification)
- Old owo-lib configuration migration to Fzzy Config and optional mod integrations
- Performance with many mobs and structure rules enabled
