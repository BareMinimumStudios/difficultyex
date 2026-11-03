# Verification and remaining acceptance

[Documentation](README.md) · [Remaining work](../TODO.md) · [Interactive checks](FINAL_ACCEPTANCE.md)

The 1.21.1 port remains in development. This page consolidates the completed checks; authenticated multiplayer, full visual acceptance and human gameplay are still pending. Historical investigation details remain in Git history.

## Automated checks

- 69 JVM tests across 14 suites cover arithmetic, bounds, migration, nameplate policies, config synchronization metadata and generated loader dependency ranges.
- 20 release-tool tests cover version/notes validation, artifact selection, hashes and verification of prepared or downloaded bundles.
- Both loaders build with Java 21. IDE inspection errors were resolved by compiling unchanged mixin/client Java in `src/loaderShared/java` against each loader's full game classes.
- [GitHub run 40955966398](https://github.com/BareMinimumStudios/difficultyex/actions/runs/40955966398) supplied verified XML reports with 69 tests and no failures, errors or skips. [Run 41046680267](https://github.com/BareMinimumStudios/difficultyex/actions/runs/41046680267) also passed build, release checks and both artifact uploads.

## Packaged servers and persistence

Actual local and downloaded CI JARs passed dedicated-server startup, operator commands, save and full restart on Minecraft 1.21.1 with Fabric Loader 0.20.1 and NeoForge 21.1.256. Both preserved a protected level-20 zombie at 26/52 HP. Development NeoForge is pinned separately to 21.1.26.

A stopped-file edit/restart preserved that mob's level and absolute 26 HP while a new health rate changed its maximum to 68. New zombies used level 23, blacklisted cows remained unscaled, and the configured command cap rejected level 71 when the maximum was 70.

Fourteen real mob types passed ten injured-health NBT cycles and a zero-health cycle on both loaders: 280 injured cycles and 28 zero-health cycles. Modifiers did not stack; unrelated attribute modifiers survived config changes. These instances were explicitly initialized, not naturally spawned or tracked clients.

## Configuration and migration

A copied production 1.20.1 config preserved 12 dimension rules, 130 biome rules and 234 nameplate blacklist entries. Invalid rule entries were corrected without losing valid ones. Migration regressions cover file precedence, preserving originals and existing modern files, blocked destinations and safe retries.

Both loaders passed ten real-library serialization rounds across all 20 gameplay fields while preserving seven independent local preferences, full JSON5 reloads and malformed-input rejection. NeoForge's real configuration handler rejected unauthorized, forged and malformed updates and accepted operator updates. These were config-object and handler checks with no authenticated clients connected.

## Combat and XP

Scripted-player checks passed difficulty scaling, Sharpness, armor, Protection, eligible XP pickup, Mending and non-player kill eligibility. PlayerEx chunk XP suppression can reduce collectible rewards. Human input, normal cooldown timing and multiplayer gameplay remain pending.

## Structures, visibility and performance

Both loaders passed loaded-reference/origin unload/load/unload checks without implicitly loading absent origins. Existing mob levels remained stable. Generated structure variants and inclusive geometry boundaries were checked.

Real server team combinations, viewer-relative invisibility, line of sight and distance inputs passed. A 500-mob, 600-tick empty-player sample passed; it is not a populated-world benchmark. Connected-player chunk streaming and actual rendering remain pending.

## Client and command checks

NeoForge singleplayer displayed level, translated HP and the ten-segment bar; applying the master nameplate toggle saved and changed the display immediately. Both loader client startup checks passed. Broader visual cases and independent preferences on two actual clients remain pending.

Both loaders' real command dispatchers passed permission denial, invalid numeric/target rejection, configured cap endpoints and injured/dead health preservation. The validator displayed 20 details and logged all 25 fixture findings without mutating settings.

## Release readiness

Both packaged JARs include required licenses and no temporary QA classes. Downloaded CI bundles matched GitHub's ZIP digests and their own JAR/note manifests. Workflow syntax, documentation links and desktop/mobile documentation previews passed. Uploads remain manual; credentials, final version/channel approval and actual platform acceptance are not verified. See [release preparation](RELEASING.md).

Use the [interactive run sheet](FINAL_ACCEPTANCE.md) for remaining checks. Unchecked items are not established by the server, codec or scripted-player results above.
