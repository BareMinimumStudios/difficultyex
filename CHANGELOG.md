# Changelog

## [Unreleased] - Minecraft 1.21.1 port

- Began the Fabric + NeoForge migration with Cloche, Java 21, Kotlin, and Gradle 9.8.0.
- Removed owo-lib and the associated KSP/generated configuration layer.
- Added safe, first-run migration of supported owo-lib JSON5 settings into Fzzy Config when no modern config exists. The original file is preserved. Validated with four tests and NeoForge dedicated-server startup.
- Hardened legacy migration against malformed blacklists and level maps, fractional or overflowing integer settings, and non-finite rates. Valid entries survive; invalid entries are logged and skipped. Seven migration tests and all 53 tests pass.
- Require positive values in all eight entity/dimension/biome/structure level maps on both loaders. Added optional-editor descriptions for key formats, rule conflicts, and loaded structure origins; JSON5 keys and positive integer limits remain unchanged.
- Fixed operator level changes healing injured mobs to 1 HP or reviving zero-health mobs. The setter now preserves fractional and zero health, with safe finite arithmetic and three regressions. Both loader builds and all 56 tests pass.
- Fixed nameplate preferences being included in server synchronization: Fzzy Config 0.7.7 reads Kotlin property annotations, so field-only `@NonSync` was insufficient. Added property annotations on both loaders and a fourth cross-loader guard. All 57 tests and 15 background NeoForge runtime scenarios pass.
- Added explicit Minecraft 1.21.1 compatibility bounds to both packaged JARs, Java 21 for Fabric, and the NeoForge loader minimum. Reproduced the unchanged public PlayerEx 5.0.1 Fabric startup blocker in isolation.
- Corrected the generated NeoForge Minecraft range from invalid `[1.21.1,1.21.1]` to `[1.21.1,1.21.2)`. Added a Maven-parser regression against generated metadata; all 58 tests pass.
- Passed standalone packaged Fabric and NeoForge startup, level commands, and protected zombie save/reload with public dependencies. Public PlayerEx 5.0.1 works in the packaged Fabric environment; the earlier descriptor failure remains limited to the development setup.
- Adopted Fzzy Config and the published PlayerEx, Data Attributes, and Remnant dependency artifacts.
- Converted plain Fzzy Config values to validated GUI-editable entries in organized progression, mob, world, structure and nameplate groups. Kept the existing JSON5 keys; nameplate preferences are now `@NonSync` and remain client-local. Tested loading an existing JSON5 file and a zombie minimum-level map override on a NeoForge development server.
- Prevented large finite player-level formula results from overflowing the averaging step. An exact decimal fallback preserves cancellation and small remaining contributions before normal integer rounding and area limits. Added three regression tests; all 50 tests pass.
- Verified the existing Fzzy Config editor and file persistence in NeoForge singleplayer: cow nameplates at levels 1 and 20, ten-segment health bar, translated HP, and live enabling/disabling. Use Changes > Apply Changes to save GUI edits. DifficultyEx requires robust configuration, not a custom configuration screen; no built-in NeoForge Mods-button bridge is included.
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

### Additional background QA (2026-10-09)

- Verified a copied production legacy config, automatic correction of all eight level-rule maps, three additional generated structure variants, and nine spawn initialization workloads on NeoForge.
- Documented the Fabric development injector mismatch and rejected launch-setting experiments. No runtime workaround is shipped.
