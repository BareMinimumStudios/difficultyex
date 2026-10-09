# Changelog

## [Unreleased] - Minecraft 1.21.1 port

- Began the Fabric + NeoForge migration with Cloche, Java 21, Kotlin, and Gradle 9.8.0.
- Removed owo-lib and the associated KSP/generated configuration layer.
- Added safe, first-run migration of supported owo-lib JSON5 settings into Fzzy Config when no modern config exists. The original file is preserved. Validated with four tests and NeoForge dedicated-server startup.
- Adopted Fzzy Config and the published PlayerEx, Data Attributes, and Remnant dependency artifacts.
- Converted plain Fzzy Config values to validated GUI-editable entries in organized progression, mob, world, structure and nameplate groups. Kept the existing JSON5 keys; nameplate preferences are now `@NonSync` and remain client-local. Tested loading an existing JSON5 file and a zombie minimum-level map override on a NeoForge development server.
- Reimplemented persistent, synchronized mob levels and attribute scaling in shared sources.
- Fixed loaded scaled mobs losing saved health above vanilla maximum: preserve the original NBT health and restore it after their level modifiers are reapplied. Verified by a live NeoForge save/restart test.
- Added cross-loader build automation and opt-in mc-publish workflow.
- Reintroduced cross-loader mob nameplates with level and ten-segment health indicators, using synced mob data and vanilla rendering.
- Fixed forced mob level-nameplates bypassing vanilla team name-tag rules and viewer-relative invisibility. All display options disabled now leaves vanilla nameplates unchanged. Added four name-tag visibility tests and passed the NeoForge client startup check.
- Added structure-specific level bounds with a loaded-chunk-only search radius, consistent rule precedence, and four automated bounds tests. Verified inside and outside a generated taiga village, including conflicting minimum and maximum structure rules.
- Centralized horizontal structure bounds/chunk-range geometry with overflow-safe arithmetic and five edge-case tests. Live-tested exact inclusive X/Z edges, one-block-outside positions, and a bounding-box corner at radius 16 in a generated taiga village.
- Extracted deterministic XP reward arithmetic into a testable shared helper, with normalization of non-finite configured rates and saturation for large rewards.
- Fixed XP scaling for mob subclasses that override `getBaseExperienceReward` without calling `Mob` (notably passive animals). Scaling now intercepts the base reward in `LivingEntity.getExperienceReward` before enchantment adjustments, while leaving non-mob entities unchanged. Validated on NeoForge with actual orbs from fake-player-attributed zombie and cow kills.
- Updated developer credit to **pokesmells** and moved Java/Kotlin packages to `dev.pokesmells.difficultyex`.
- Adopted Bare Minimum Studios' BML v1.0; preserved the prior MIT notice and bundled both notices in the Fabric and NeoForge JARs.

This is an **in-progress port**. Runtime testing, client nameplates, structure rules, and full feature parity with the 1.20.1 version are not yet complete.

## Earlier changes

- Fixed an issue where Crunch was not packaged into the mod.
