# Command and saved-level QA, October 9, 2026

## Functional changes

- `/difficultyex validate` checks the active server configuration against its registered dimensions, biomes, structures and entities. It reports inactive IDs, malformed or unmatched entity regexes, formula compile failures, conflicting global bounds and non-finite rates. It requires permission level two, changes no settings, limits chat output to 20 findings, and logs all findings. Nameplate blacklist diagnostics refer to the server's copy, not connected clients' independent preferences. This checks active settings, not an edited file awaiting reload; formula syntax validation does not prove finite results for every possible player level.
- `/difficultyex inspect` reports fractional health rather than truncating a living mob to zero HP. It includes the vanilla integer armor value, precise armor attribute and attack attribute. Mobs without an attack attribute report `n/a`.
- Saved mob levels can now be read from the prior CCA compound format without installing CCA. Modern `difficultyex_level` data takes priority. Non-integer and non-positive legacy values are ignored. Subsequent saves use the modern tag.

## NeoForge real-runtime results

A disposable NeoForge 21.1.26 server passed six operator command scenarios: a clean active config; four deliberate findings without changing settings; permission-zero rejection; a cow at 0.5 HP reporting fractional health and attack `n/a`; a level-20 zombie reporting attack 9.0 and armor attribute 5.2; and rejection of an override above the global maximum without changing the zombie's level.

The same runtime loaded a synthetic prior-format zombie at level 25, restored health 30/60, wrote modern level 25 on save, and preserved level and health on reloading that save. It left input NBT unchanged, preferred modern level 10 over legacy 25, rejected zero or malformed modern data without falling back to stale legacy data, and ignored malformed/negative/missing legacy levels.

The temporary command probe was deleted and the NeoForge entrypoint restored before the final clean build.

## Fabric packaged results

The clean distributable Fabric JAR started with Loader 0.19.5 and the pinned public dependencies. The active-config check passed. A protected zombie summoned with the legacy component fixture displayed level 25, health 30/60, armor 6 and attack 10.5. Entity data contained modern `difficultyex_level:25`. After save, graceful stop and full restart, the same zombie still reported level 25 and 30/60 health. A passive cow inspection safely reported attack `n/a`. Its initial supplied health was replaced by normal fresh-spawn healing, so this was not a Fabric fractional-health assertion.

The first sandboxed launcher attempt failed because Windows denied loopback sockets. Restarting the isolated server with authorized socket access passed. The final two runs stopped gracefully, and test entities and the force-load ticket were removed.

## Source and validation limits

The prior repository implementation registered `difficultyex:entity_data` and wrote an Endec `DATA` compound containing integer `level`. CCA's [1.20.1 container implementation](https://raw.githubusercontent.com/Ladysnake/Cardinal-Components-API/1.20.1/cardinal-components-base/src/main/java/dev/onyxstudios/cca/internal/base/AbstractComponentContainer.java) stores components under `cardinal_components`. Tests used that verified fixture shape; no production world was opened or upgraded. This is entity-level compatibility, not a guarantee that an entire 1.20.1 modpack world upgrades safely. The older CCA list format is not supported.

IDE compilation passed. The complete clean test/build passed 63 tests with zero failures, errors or skips. Five new tests use the real Crunch parser and cover active-config diagnostics. Both loader JARs contain the studio and inherited MIT licenses, and no temporary QA classes.

MCDev still reports six TAIL-target inspection errors against Cloche's common API stub for MobLevelMixin. The unchanged baseline produced the identical six errors. No injector selector was changed. Mixin MCP bytecode checks found the actual return instructions in both game artifacts, and real NeoForge and packaged Fabric loading exercised the changed reader successfully. The IDE stub-inspection limitation remains unresolved; inspection success is not claimed.

Both disposable server directories and ignored QA run configurations were removed. The original development config hash remained `B441095EEA5B7ED42E0DDB48C9DF84F60E8457F0A383E45725BF8A3D595D3F3C`; RCON is disabled. Authenticated clients, rendering, human combat/XP, and the Fabric named-development discrepancy remain separate acceptance work.
