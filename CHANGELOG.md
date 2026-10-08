# Changelog

## [Unreleased] - Minecraft 1.21.1 port

- Began the Fabric + NeoForge migration with Cloche, Java 21, Kotlin, and Gradle 9.8.0.
- Removed owo-lib and the associated KSP/generated configuration layer.
- Adopted Fzzy Config and the published PlayerEx, Data Attributes, and Remnant dependency artifacts.
- Reimplemented persistent, synchronized mob levels and attribute scaling in shared sources.
- Added cross-loader build automation and opt-in mc-publish workflow.

This is an **in-progress port**. Runtime testing, client nameplates, structure rules, and full feature parity with the 1.20.1 version are not yet complete.

## Earlier changes

- Fixed an issue where Crunch was not packaged into the mod.
