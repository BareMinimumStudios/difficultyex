# DifficultyEx 1.21.1 Port Status

Development snapshot — October 8, 2026. Not release-ready.

## Working foundation

- Builds on Gradle 9.8.0, Cloche 0.19.13, Java 21, and Kotlin.
- Fabric and NeoForge project layouts; Fzzy Config, no owo-lib/KSP.
- Safe one-time legacy owo-lib JSON5 import on both loaders, only when a modern config is absent. 4 conversion/file-safety unit tests pass. **NeoForge runtime migration tested:** synthetic legacy defaults (starting level 17, maximum 200, structure radius 25, disabled nameplates) were imported before Fzzy Config initialization; server reached `Done`. Original development config restored byte-for-byte and temporary test file deleted. Legacy visual offset/scale/colors and per-player difficulty enum are intentionally not migrated.
- Public PlayerEx 5.0.1, Data Attributes 3.0.0, Remnant 3.0.0 dependencies.
- Shared mob-level persistence, synchronization and attribute-scaling implementation.
- **Verified live on NeoForge:** `/difficultyex inspect` and `/difficultyex set` both execute; an invulnerable test zombie scaled from level 1 (21.6 maximum health) to level 10 (36) and level 20 (52). Changing level at half health preserved the ratio (18/36 → 26/52).
- **Persistence regression fixed and retested:** the initial restart retained level 20 and maximum health 52 but incorrectly clamped saved health from 26 to 20. The fix captures raw NBT Health during load and restores it after modifiers, giving level 20, **26/52 health**, and 52 maximum health after the next save/restart. The test entity and forced chunk ticket were cleaned up.
- Shared XP reward calculation now guards non-finite configured rates and saturates oversized results; four arithmetic boundary tests pass. Actual XP-orb drop behavior after player kills is not yet validated.
- Client-side mob nameplate mixins compiled and packaged for both loaders: level, ten-segment textual health bar and separately togglable health numbers, hostile-only, distance, visibility and blacklist options. Visual behavior still needs in-game verification.
- Structure-specific minimum/maximum levels and configurable horizontal radius (loaded chunks only; cap 128), with deterministic combined bounds and four passing unit tests.
- **Verified live on NeoForge (October 8):** located a naturally generated `minecraft:village_taiga` at X=0, Z=608. A zombie inside its structure influence gained level 30 from `structureStartingLevels`; an outside control zombie remained level 1. After reloading with a conflicting level-20 `structureMaximumLevels` entry, a newly spawned village zombie was level 20 (maximum takes precedence). Test mobs and forced chunk tickets were removed; original development config restored.
- `gradlew.bat clean test build` passed for both loaders; JUnit XML reports **8 tests** (four level-bounds tests and four XP-math tests), 0 failures, 0 errors.
- NeoForge client smoke test initialized OpenGL 4.6, loaded DifficultyEx resources/config and reached texture atlas loading without a startup mixin failure. Test client was closed; actual in-world nametag rendering remains untested.
- New structure fields were missing in the existing development config on first load; Fzzy Config reported missing keys and wrote their defaults into `run/config/difficultyex/config.json5`.
- MixinMCP Gradle decompile plugin 1.5.0 configured.
- Developer metadata and all package paths use `pokesmells` (`dev.pokesmells.difficultyex`); former source namespace no longer appears in Java/Kotlin code.
- Repository root now uses BML v1.0 from the studio license, retaining the inherited MIT notice in `docs/ORIGINAL_MIT_LICENSE.txt`. Both license texts are included in each loader JAR.
- `gradlew.bat build` succeeds for both loaders after adding shared operator-only `/difficultyex inspect` and `/difficultyex set` commands, with loader-specific registrations. The setter retains the target mob's health percentage.
- Git development work is restricted to the `1.21.1` branch; the push/publication pipeline remains manual and is now explicitly gated to that branch.
- NeoForge 21.1.26 development server successfully initialized DifficultyEx + all dependencies, loaded Fzzy Config, generated a world and reached the `Done` state. Its test server process was stopped.

## Fabric integration blocker

Verified again after the `dev.pokesmells` package migration: the **public PlayerEx 5.0.1 Fabric JAR** crashes during `runFabric1211Server` at `playerex.mixins.json:ItemStackGameplayMixin`. The `playerex$preserveBrokenEntity` injector declares a `LivingEntity` parameter, but the Minecraft 1.21.1 target method requires `ServerPlayer`. This must be corrected in PlayerEx and released as a new compatible artifact before end-to-end DifficultyEx tests on Fabric.

## Outstanding migration items

- In-game visual verification/polish of new client nameplates and health indicators; GeckoLib/Traveler's Titles optional integrations.
- More structure variants, radius-boundary checks, real-world owo-lib configuration examples to validate the synthetic-file migration, and feature parity with 1.20.1.
- Continue runtime testing for spawn-level calculation against player progression, structure-radius boundaries, modified damage and armor, player-kill XP drops, and multiplayer synchronization. The NeoForge commands, attribute scaling, persistence and one village's structure bounds have been validated.
- Test packaged builds in isolated copies of the user's Fabric and NeoForge server setups, using the **publicly released** versions rather than the versions originally installed on the servers.
- mc-publish project IDs verified from publicly visible release pages: Modrinth `qdJ4GLvL` and CurseForge `1387673`; still verify credentials, workflow syntax, dependency metadata and release prerequisites before manually publishing.

The user's server copies were not changed. This update is local to the `1.21.1` working tree; no release or push was performed as part of this pass.
