# DifficultyEx 1.21.1 Port Status

Development snapshot — October 8, 2026. Not release-ready.

## Working foundation

- Builds on Gradle 9.8.0, Cloche 0.19.13, Java 21, and Kotlin.
- Fabric and NeoForge project layouts; Fzzy Config, no owo-lib/KSP.
- Public PlayerEx 5.0.1, Data Attributes 3.0.0, Remnant 3.0.0 dependencies.
- Shared mob-level persistence, synchronization and attribute-scaling implementation.
- MixinMCP Gradle decompile plugin 1.5.0 configured.
- Developer metadata and all package paths use `pokesmells` (`dev.pokesmells.difficultyex`); former source namespace no longer appears in Java/Kotlin code.
- Repository root now uses BML v1.0 from the studio license, retaining the inherited MIT notice in `docs/ORIGINAL_MIT_LICENSE.txt`. Both license texts are included in each loader JAR.
- `gradlew.bat build` succeeds for both loaders after adding shared operator-only `/difficultyex inspect` and `/difficultyex set` commands, with loader-specific registrations. The setter retains the target mob's health percentage.
- Git development work is restricted to the `1.21.1` branch; the push/publication pipeline remains manual and is now explicitly gated to that branch.
- NeoForge 21.1.26 development server successfully initialized DifficultyEx + all dependencies, loaded Fzzy Config, generated a world and reached the `Done` state. Its test server process was stopped.

## Fabric integration blocker

Verified again after the `dev.pokesmells` package migration: the **public PlayerEx 5.0.1 Fabric JAR** crashes during `runFabric1211Server` at `playerex.mixins.json:ItemStackGameplayMixin`. The `playerex$preserveBrokenEntity` injector declares a `LivingEntity` parameter, but the Minecraft 1.21.1 target method requires `ServerPlayer`. This must be corrected in PlayerEx and released as a new compatible artifact before end-to-end DifficultyEx tests on Fabric.

## Outstanding migration items

- Client nameplates, level text and health bars; GeckoLib/Traveler's Titles optional integrations.
- Structure-based level rules, configuration migration, and behavior parity with 1.20.1.
- Test new operator commands in-game, spawn-level calculation, entity modifiers, XP rewards, saves/reloads and multiplayer sync. Compilation alone does not validate command execution.
- Test packaged builds in isolated copies of the user's Fabric and NeoForge server setups, using the **publicly released** versions rather than the versions originally installed on the servers.
- mc-publish project IDs verified from publicly visible release pages: Modrinth `qdJ4GLvL` and CurseForge `1387673`; still verify credentials, workflow syntax, dependency metadata and release prerequisites before manually publishing.

The user's server copies were not changed. This update is local to the `1.21.1` working tree; no release or push was performed as part of this pass.
