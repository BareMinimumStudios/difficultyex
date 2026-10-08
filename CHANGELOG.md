# Changelog

## [Unreleased] - Minecraft 1.21.1 port

- Began the Fabric + NeoForge migration with Cloche, Java 21, Kotlin, and Gradle 9.8.0.
- Removed owo-lib and the associated KSP/generated configuration layer.
- Adopted Fzzy Config and the published PlayerEx, Data Attributes, and Remnant dependency artifacts.
- Reimplemented persistent, synchronized mob levels and attribute scaling in shared sources.
- Fixed loaded scaled mobs losing saved health above vanilla maximum: preserve the original NBT health and restore it after their level modifiers are reapplied. Verified by a live NeoForge save/restart test.
- Added cross-loader build automation and opt-in mc-publish workflow.
- Reintroduced cross-loader mob nameplates with level and ten-segment health indicators, using synced mob data and vanilla rendering.
- Added structure-specific level bounds with a loaded-chunk-only search radius, consistent rule precedence, and four automated bounds tests. Verified inside and outside a generated taiga village, including conflicting minimum and maximum structure rules.
- Extracted deterministic XP reward arithmetic into a testable shared helper, with normalization of non-finite configured rates and saturation for large rewards.
- Updated developer credit to **pokesmells** and moved Java/Kotlin packages to `dev.pokesmells.difficultyex`.
- Adopted Bare Minimum Studios' BML v1.0; preserved the prior MIT notice and bundled both notices in the Fabric and NeoForge JARs.

This is an **in-progress port**. Runtime testing, client nameplates, structure rules, and full feature parity with the 1.20.1 version are not yet complete.

## Earlier changes

- Fixed an issue where Crunch was not packaged into the mod.
