# Background runtime QA, October 9, 2026

A disposable loopback-only NeoForge 21.1.26 dedicated server ran 15 scenarios with public PlayerEx 5.0.1, Data Attributes 3.0.0, Remnant 3.0.0, and Fzzy Config 0.7.7. Java 21 and IntelliJ's Gradle run configuration were used. No game window or authenticated player account was involved. The server stopped gracefully after the probe.

## Passed scenarios

| Scenario | Observed result |
| --- | --- |
| Entity minimum map | Positive value accepted; zero/negative rejected, corrected to 1 with error; clone retains validation |
| Entity maximum map | Same validation/correction behavior |
| Dimension minimum map | Same validation/correction behavior |
| Dimension maximum map | Same validation/correction behavior |
| Biome minimum map | Same validation/correction behavior |
| Biome maximum map | Same validation/correction behavior |
| Structure minimum map | Same validation/correction behavior |
| Structure maximum map | Same validation/correction behavior |
| JSON5 codec round trip | Progression, area maps, and local preferences preserved through real Fzzy codecs |
| Independent preference serialization | Server starting level 33 applied to two config objects; distances 4 and 20 and different enabled values remained local |
| Fractional-health command | Actual dispatcher changed cow level 20 to 1; 0.5 HP scaled by its old/new maximum ratio and stayed below 1 HP |
| Zero-health command | Actual dispatcher set a zero-health cow to level 20 without reviving it |
| Operator maximum restriction | Global maximum 10 rejected level 11 without changing the mob level |
| Combined spawn bounds | Starting 7, dimension minimum 30, entity minimum 60, global maximum 40 produced level 40 and cow maximum health 42 |
| Modifier idempotence | Reapplying level 20 three times retained zombie health 52, attack 9, and armor 5.2 without stacking modifiers |

## Sync bug found and fixed

Before the fix, the real serializer replaced a local nameplate distance of 4 with the server value 9. Fzzy Config 0.7.7 checks `KProperty.annotations`; JVM field-only `@NonSync` annotations are insufficient. Both loader configurations now declare `@property:NonSync` and retain `@field:NonSync`. The corrected runtime probe passed. A new bytecode regression checks Kotlin property annotation carriers for all seven preferences, alongside the existing field and schema checks.

This uses actual config objects and serialization flags in a running server. It does not establish authenticated network transport, reconnect persistence, or client GUI permissions.

## Build and packaged metadata

The final IDEA clean/test/build passed 57 tests with zero failures, errors, or skips. Both JARs contain the studio license and inherited MIT notice. Neither includes the disposable runtime probe or QA artifacts. Both explicitly require Minecraft 1.21.1; Fabric also declares Java 21, and NeoForge declares loader 21.1.26 or newer. Existing required mod dependencies and bundled Crunch remain present.

These checks inspect the distributable metadata and contents. They do not replace startup testing of packaged JARs in standalone server copies.

## Fabric blocker reproduced

An isolated Fabric development server failed in public PlayerEx 5.0.1 at `playerex.mixins.json:ItemStackGameplayMixin`. The `playerex$preserveBrokenEntity` injector uses `LivingEntity` where the Minecraft target requires `ServerPlayer`. The public Modrinth version list still reports 5.0.1 as the latest 1.21.1 release. No dependency was replaced or patched locally to conceal this blocker.

## Cleanup and remaining acceptance

The probe source and temporary entry-point registration were removed before the clean build. Disposable server directories and local QA run configurations were removed. The original development config remained byte-for-byte unchanged; RCON stayed disabled, no development game process remained, and test listeners were closed. No user server copy was changed. No commit or push was performed.

Remaining: two authenticated clients and reconnect behavior; player combat/equipment/enchantments and XP pickup; client visual and title-screen permission checks; additional structure variants and real movement; production-origin legacy files; standalone packaged-server acceptance; optional integrations and feature parity.
