# Packaged server acceptance, October 9, 2026

The actual distributable DifficultyEx JARs were tested in fresh copies of the user's reference server launch libraries. No development class directories, temporary QA mod, or patched dependency was loaded. Reference worlds, configs, mods, and launch files were not modified.

| Environment | Startup | Commands | Protected mob after full restart |
| --- | --- | --- | --- |
| Minecraft 1.21.1, NeoForge 21.1.256, Java 21.0.10 | Done | inspect/set, level 10 to 20, health 18/36 to 26/52 | Level 20, health 26/52, armor 5 |
| Minecraft 1.21.1, Fabric Loader 0.19.5, Java 21.0.10 | Done | Same commands and health behavior | Level 20, health 26/52, armor 5 |

Dependencies were the published PlayerEx 5.0.1, Data Attributes 3.0.0, Remnant 3.0.0, and Fzzy Config 0.7.7 artifacts pinned by the port. NeoForge used the public KotlinForForge 5.11.0 all-JAR distribution. Fabric used Fabric API 0.116.15+1.21.1 and Fabric Language Kotlin 1.13.7+kotlin.2.2.21.

## Metadata failure caught

The first standalone NeoForge load rejected the previous generated Minecraft range `[1.21.1,1.21.1]`. Maven prohibits identical interval boundaries. The build now generates `[1.21.1,1.21.2)` for NeoForge and the equivalent bound for Fabric. A new regression parses the actual generated NeoForge metadata with Maven's version parser, accepts 1.21.1, and rejects 1.21, 1.21.2, and 1.22. The complete clean build passed 58 tests with zero failures, errors, or skips.

The test setup initially copied KotlinForForge's Gradle library artifact, which is not its installable server distribution. It was replaced by the published 5.11.0 all-JAR, verified against the Modrinth SHA-1 before loading.

## Persistence fixture

A tagged zombie was spawned with NoAI, NoGravity, Invulnerable, and PersistenceRequired. Daylight progression was disabled and time set to midnight to prevent ambient damage. The operator setter changed level 10 to 20 after setting health to 18/36; inspection returned 26/52. After saving, stopping, and restarting, both packaged servers returned level 20, health 26/52, armor 5. The inspect command truncates armor to an integer.

An earlier unprotected NeoForge fixture lost health between checks, so it was excluded from the acceptance result. The protected repeat removed that confound. Fabric used one temporary force-loaded chunk to keep the fixture available after restart; the ticket was removed during cleanup.

## Fabric diagnosis corrected

Public PlayerEx 5.0.1 successfully loads and supports these scenarios in the standalone Fabric environment. The earlier LivingEntity/ServerPlayer descriptor exception occurred in the Cloche development launch with Fabric Loader 0.19.3. The difference could involve mappings, loader versions, or other development setup details; its cause has not been isolated. An upstream public-release defect is not established by the development failure.

## Cleanup and limits

The final command/persistence runs stopped through the normal stop command and saved all dimensions. Initial launches without retained console input were stopped and restarted before command acceptance. Both test servers bound only to loopback, on temporary ports 25587/25588, with online mode enabled and RCON disabled. No authenticated player joined.

Test mobs and the Fabric force-load ticket were removed. Disposable server copies, libraries, configs, and worlds were deleted afterward. The repository development config retained its original hash; no game process or listener remained. No commit or push was performed.

Remaining acceptance: authenticated clients and network/reconnect behavior; player combat/equipment/enchantments and XP collection; nameplate visuals and client permissions; broader structures and performance; production-origin legacy migration; optional integrations and feature parity. The development-launch discrepancy is a separate unresolved tooling issue.
