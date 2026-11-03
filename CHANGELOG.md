# Changelog

## 1.1.0: Minecraft 1.21.1

- Ported to Fabric and NeoForge with Java 21 and shared gameplay sources.
- Replaced owo-lib with Fzzy Config, validated JSON5 fields, an optional editor and seven client-local nameplate preferences.
- Added non-destructive migration of supported 1.20.1 settings and saved mob levels.
- Implemented persistent synchronized mob levels, configurable stat/XP scaling and entity/dimension/biome/loaded-structure bounds.
- Added operator validation, inspection and level-setting commands.
- Restored level/health nameplates with team and invisibility policies, cached patterns and safe health formatting.
- Fixed saved-health clamping, fractional/zero-health overrides, passive-mob XP hooks and numeric overflow handling.
- Resolved the Fabric development launch mismatch with Loader 0.20.1 and IDE injection inspections through loader shared sources.
- Added cross-loader tests, manual publishing, focused release notes and verified release bundles with hash manifests.
- Refreshed project branding and simplified release documentation.
- Adopted BML 1.0 and preserved the inherited MIT notice in both loader JARs.

## Earlier changes

- Fixed Crunch not being packaged into the mod.
