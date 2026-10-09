# Changelog

## [Unreleased] - Minecraft 1.21.1 port

- Began the Fabric + NeoForge migration with Cloche, Java 21, Kotlin, and Gradle 9.8.0.
- Removed owo-lib and the associated KSP/generated configuration layer.
- Added safe, first-run migration of supported owo-lib JSON5 settings into Fzzy Config when no modern config exists. The original file is preserved. Validated with four tests and NeoForge dedicated-server startup.
- Adopted Fzzy Config and the published PlayerEx, Data Attributes, and Remnant dependency artifacts.
- Converted plain Fzzy Config values to validated GUI-editable entries in organized progression, mob, world, structure and nameplate groups. Kept the existing JSON5 keys; nameplate preferences are now `@NonSync` and remain client-local. Tested loading an existing JSON5 file and a zombie minimum-level map override on a NeoForge development server.
- Added a cross-loader bytecode regression guard for Fzzy Config sync metadata. The three new tests verify seven runtime-visible `@NonSync` nameplate fields, server-authoritative progression fields without that annotation, and identical Fabric/NeoForge config field types. The Gradle `test` task now compiles both loader configs before running the checks.
- Reimplemented persistent, synchronized mob levels and attribute scaling in shared sources.
- Verified PlayerEx-driven spawn levels in eight NeoForge runtime scenarios using transient fake players with genuine PlayerEx states: fallback, one/two players, distance exclusion, exact radius, 3D distance, and per-player `x*2` formula. Extracted the deterministic distance/averaging policy for regression tests; spectator-mode players no longer affect mob difficulty.
- Fixed loaded scaled mobs losing saved health above vanilla maximum: preserve the original NBT health and restore it after their level modifiers are reapplied. Verified by a live NeoForge save/restart test.
- Added cross-loader build automation and opt-in mc-publish workflow.
- Reintroduced cross-loader mob nameplates with level and ten-segment health indicators, using synced mob data and vanilla rendering.
- Fixed nameplate language formatting for Minecraft 1.21.1: health translations now use supported `%s` substitutions instead of unsupported `%d`, and level/health colors use styled text components rather than misencoded legacy color codes. Added three packaged-language regression tests and passed a NeoForge client startup smoke test with the corrected resources.
- Verified custom mob levels survive the vanilla entity-metadata packet stream codec and apply to a second entity instance for both initial snapshot (level 12) and dirty update (level 27) on a live NeoForge development server. The temporary QA command was removed; authenticated client-render tests remain pending.
- Cached up to 256 compiled nameplate/spawn blacklist patterns (including invalid-regex results) instead of recompiling them per rendered mob; reset the cache on config updates. Made nameplate health bars show at least one segment for living mobs and less than a full bar for injured mobs, with tested non-finite health handling.
- Fixed forced mob level-nameplates bypassing vanilla team name-tag rules and viewer-relative invisibility. All display options disabled now leaves vanilla nameplates unchanged. Added four name-tag visibility tests and passed the NeoForge client startup check.
- Added structure-specific level bounds with a loaded-chunk-only search radius, consistent rule precedence, and four automated bounds tests. Verified inside and outside a generated taiga village, including conflicting minimum and maximum structure rules.
- Centralized horizontal structure bounds/chunk-range geometry with overflow-safe arithmetic and five edge-case tests. Live-tested exact inclusive X/Z edges, one-block-outside positions, and a bounding-box corner at radius 16 in a generated taiga village.
- Prevented structure reference lookups from synchronously loading *unloaded structure-origin chunks* during mob spawning. Vanilla `startsForStructure` could load origin chunks even when nearby reference chunks were already loaded. The replacement uses non-loading `getChunkNow` for both and skips a structure rule if its origin is absent; tested both cases live on NeoForge.
- Extracted deterministic XP reward arithmetic into a testable shared helper, with normalization of non-finite configured rates and saturation for large rewards.
- Extracted health/armor/attack modifier arithmetic into a shared tested helper with explicit non-finite rate rejection and modifier caps. Validated NeoForge combat damage directly: level-1 versus level-20 zombies dealt 3.3 versus 9.0 damage to cows; armored zombies received 4.92 versus 4.50 from identical 5-point hits.
- Fixed XP scaling for mob subclasses that override `getBaseExperienceReward` without calling `Mob` (notably passive animals). Scaling now intercepts the base reward in `LivingEntity.getExperienceReward` before enchantment adjustments, while leaving non-mob entities unchanged. Validated on NeoForge with actual orbs from fake-player-attributed zombie and cow kills.
- Updated developer credit to **pokesmells** and moved Java/Kotlin packages to `dev.pokesmells.difficultyex`.
- Adopted Bare Minimum Studios' BML v1.0; preserved the prior MIT notice and bundled both notices in the Fabric and NeoForge JARs.

This is an **in-progress port**. Runtime testing, client nameplates, structure rules, and full feature parity with the 1.20.1 version are not yet complete.

## Earlier changes

- Fixed an issue where Crunch was not packaged into the mod.
