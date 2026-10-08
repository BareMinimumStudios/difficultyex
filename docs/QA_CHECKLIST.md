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

## Still requiring acceptance testing

- Fabric server/client startup after a compatible public PlayerEx release fixes its 1.21.1 mixin descriptor
- Actual multiplayer mob-level synchronization and client nameplate rendering, including health display toggles
- Structure-specific minimum/maximum rules in and around naturally generated structures
- XP drops after mob death, modified mob damage and armor in combat
- Old owo-lib configuration migration to Fzzy Config and optional mod integrations
- Performance with many mobs and structure rules enabled
