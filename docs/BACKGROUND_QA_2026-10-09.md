# Background acceptance, October 9, 2026

Tested on NeoForge 21.1.26 in a disposable loopback-only development server. No authenticated clients joined. Temporary probe source and server directories were removed before the final clean build.

## Production configuration migration

A copy of the user's ALBOE 1.20.1 configuration was imported through the normal startup path and loaded by Fzzy Config 0.7.7.

- All 12 dimension minimum rules and 130 biome minimum rules matched their original keys and values, including IDs for mods absent from this test installation.
- The empty structure minimum map remained empty; structure radius remained 50.
- All 234 nameplate blacklist entries survived in their original order.
- Maximum level remained 1,000,000. The legacy starting level of zero was corrected to one by Fzzy validation and saved, with a non-critical validation warning.
- The original file was unchanged. Its SHA256 before and after was `38EAABD0F9271D19BDA9A3CDEF7F75A9F37BE137A0A63788F97347D974CA2ED5`.

This verifies the tested fields of one production config. It does not establish that all old options or old mod IDs have equivalent behavior in 1.21.1. Retired visual controls and per-player difficulty selection still need a parity decision.

## Automatic file correction

Each of the eight entity/dimension/biome/structure minimum and maximum maps was edited in the disposable JSON5 file to contain valid level 25, zero, and negative five. On normal startup, every map retained 25 and corrected both invalid values to one. Fzzy emitted validation warnings and wrote the corrected values back to the file. Both runtime snapshots and saved JSON5 were checked. No GUI was involved.

## Spawn initialization workload

After 100 warm-up entities per radius, transient zombies were constructed, passed through `DifficultyEx.onEntityLoad`, checked for a valid level and finite maximum health, then discarded. Three structure rules were enabled. Each batch ran on the real server thread.

| Structure radius | 100 mobs | 1,000 mobs | 2,000 mobs |
| --- | ---: | ---: | ---: |
| 0 | 13.34 ms | 56.29 ms | 63.65 ms |
| 50 | 4.42 ms | 34.93 ms | 64.40 ms |
| 128 | 3.76 ms | 43.14 ms | 72.65 ms |

These are single-run timings that include entity construction and assertions. They are not a comparative benchmark, a populated-world tick test, or a multiplayer performance guarantee. Three structure IDs were configured, but this spawn area did not establish dense structure-reference coverage.

## Additional generated structures

The probe explicitly located, generated and loaded the required chunks. With random variation zero and radius zero, a zombie inside each bounding box received level 30; adding maximum 20 yielded level 20; a zombie one block beyond the maximum X boundary received level one. Vertical position was 150, confirming horizontal influence even for the underground mineshaft.

| Structure | Generated bounding box X / Z |
| --- | --- |
| `minecraft:mineshaft` | -170..-103 / -197..-117 |
| `minecraft:ruined_portal` | 70..80 / 32..47 |
| `minecraft:village_plains` | -292..-144 / 63..218 |

The first diagnostic used a locator's placeholder Y=0 with a full 3D structure lookup and failed to retrieve the mineshaft start. The corrected diagnostic read the generated origin chunk directly. All nine final level assertions passed. Real player movement and late origin loading remain separate checks.

## Fabric development diagnosis

The named PlayerEx dependency retains an optional literal `hurtAndBreak(...LivingEntity...)` selector. Vanilla 1.21.1 provides the corresponding four-argument method with `ServerPlayer`. Fabric development bootstrap enables refmap remapping; Mixin's permissive target pass can ignore an unmatched descriptor. This explains the observed alternate injector targeting the incompatible overload in the named development environment.

A temporary `remapRefMap=false` launch setting did not fix the issue: Fabric bootstrap sets it back to true. Setting the default mod distribution namespace to named avoided the PlayerEx failure but caused a Fabric GameTest refmap injection failure. Neither experiment is a supported fix, and neither was retained. Production packaged Fabric acceptance remains valid. A compatible development-toolchain or dependency fix still needs validation.

## Cleanup and remaining acceptance

All three completed NeoForge probe runs stopped gracefully and saved all dimensions. The development entrypoint was restored byte-for-byte. Temporary probes, local QA run configurations, and disposable server directories were removed. Original production and development configs were preserved; RCON remains disabled.

Still pending: authenticated two-client networking/reconnect/preferences, client visuals and permissions, human combat/equipment/enchantments and XP pickup, active-world performance, real movement around partially loaded structures, optional integration and 1.20.1 parity decisions, and release prerequisites. The final clean build validation is recorded in the port status.
