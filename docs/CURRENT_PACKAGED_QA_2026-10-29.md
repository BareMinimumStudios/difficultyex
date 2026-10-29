# Current packaged-server and file-restart acceptance

Verified October 29, 2026 with the actual distributable JARs from branch 1.21.1. Both isolated servers ran Java 21.0.10, public pinned dependencies and no development class directories or QA hooks.

| Environment | Initial commands and save | Full restart | File-edit restart |
| --- | --- | --- | --- |
| Fabric Loader 0.20.1, Minecraft 1.21.1 | Passed | Passed | Passed |
| NeoForge 21.1.256, Minecraft 1.21.1 | Passed | Passed | Passed |

This closes the packaged Fabric acceptance gap introduced by the Loader >=0.20 minimum. The previous Loader 0.19.5 report describes the earlier build; development and packaged acceptance now both include 0.20.1.

## Packaged save and restart

A protected, persistent zombie was set to level 10 and 18/36 HP. Setting level 20 preserved the percentage as 26/52 HP. After a full process stop and restart, both servers restored level 20 and 26/52 HP. The active-config validator passed. A temporary force-loaded chunk kept the fixture available after restart.

## Offline config edit and restart

Only the stopped disposable copies were edited: starting level 23, maximum level 70, zero random variation, health rate 0.12 and a cow blacklist. Both servers then restarted and passed:

- Existing zombie: level 20, 26/68 HP, armor attribute 5.2 and attack 9.0. The assigned level and absolute saved HP survived while the new health rate changed maximum health.
- Fresh zombie: level 23, 75.2/75.2 HP, armor attribute 5.68 and attack 9.9.
- Blacklisted cow: level 0, 10/10 HP, no attack attribute.
- Operator set to level 71: rejected with the configured range 1 to 70. Existing zombie remained level 20 and 26/68 HP.
- Active configuration: passed registry, pattern and formula diagnostics.

This establishes actual file loading and full-world restart behavior, supplementing the earlier in-memory snapshot and NBT checks. It does not establish live client config delivery.

## Artifacts and dependencies

| Artifact | SHA256 |
| --- | --- |
| DifficultyEx-1.1.0-fabric-1.21.1.jar | `3B813C63A34AD83E486E84E475374D88B303459A6BD1DF4B4415711DC6916556` |
| DifficultyEx-1.1.0-neoforge-1.21.1.jar | `2F68B6E61587D8143960728CBEC3A7E22ABF73295B51CB7969F97D984A07C5DB` |

Both runs used PlayerEx 5.0.1 with its bundled Remnant 3.0.0, Data Attributes 3.0.0 and Fzzy Config 0.7.7 for the loader. Fabric also used Fabric API 0.116.15+1.21.1 and Fabric Language Kotlin 1.13.7+kotlin.2.2.21. NeoForge used the public KotlinForForge 5.11.0 all-JAR, verified against the published Modrinth SHA-1.

The Fabric server launcher came from the official Fabric metadata service for Minecraft 1.21.1, Loader 0.20.1 and installer 1.1.2. Its SHA256 was `7638DC69EF2CECB27D1CFF1A1B0A6E2C3CED7ECE0000D584039A3F4E1386123C`. Its first attempt failed preparing the disposable remapping cache because of restricted file access, before mod loading. The repeat with access to that cache passed.

## Cleanup and limits

Every accepted run exited normally and saved all dimensions. Fixtures and force-load tickets were removed before final shutdown. Disposable server libraries, mods, configs and worlds were deleted. Reference server launch files, mods, configs and worlds were not modified. RCON stayed disabled and both test servers bound only to loopback on temporary ports 25587 and 25588.

No production source changed and no rebuild was needed: the tested JARs are the exact artifacts from the preceding clean build with 69 passing tests across 14 suites. This pass verifies those artifacts in standalone environments. Authenticated clients, network/reconnect behavior, visuals, human gameplay and broader populated-world performance remain open.
