# DifficultyEx 1.21.1 Port Status

Development snapshot — October 8, 2026. Not release-ready.

## Working foundation

- Builds on Gradle 9.8.0, Cloche 0.19.13, Java 21, and Kotlin.
- Fabric and NeoForge project layouts; Fzzy Config, no owo-lib/KSP.
- Public PlayerEx 5.0.1, Data Attributes 3.0.0, Remnant 3.0.0 dependencies.
- Shared mob-level persistence, synchronization and attribute-scaling implementation.
- MixinMCP Gradle decompile plugin 1.5.0 configured.
- `gradlew.bat clean build` completed for both loaders.
- NeoForge 21.1.26 development server successfully initialized DifficultyEx + all dependencies, loaded Fzzy Config, generated a world and reached the `Done` state. Its test server process was stopped.

## Fabric integration blocker

The **public PlayerEx 5.0.1 Fabric JAR** crashes during `runFabric1211Server` at `playerex.mixins.json:ItemStackGameplayMixin`. The `playerex$preserveBrokenEntity` injector declares a `LivingEntity` parameter, but the Minecraft 1.21.1 target method requires `ServerPlayer`. This must be corrected in PlayerEx and released as a new compatible artifact before end-to-end DifficultyEx tests on Fabric.

## Outstanding migration items

- Client nameplates, level text and health bars; GeckoLib/Traveler's Titles optional integrations.
- Structure-based level rules, configuration migration, and behavior parity with 1.20.1.
- Test spawn-level calculation, entity modifiers, XP rewards, saves/reloads and multiplayer sync.
- Test packaged builds in isolated copies of the user's Fabric and NeoForge server setups, using the **publicly released** versions rather than the versions originally installed on the servers.
- Verify mc-publish project metadata and release prerequisites before manually publishing.

The user's server copies were not changed. No GitHub push, release or commit was performed.
