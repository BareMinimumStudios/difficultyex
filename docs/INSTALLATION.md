# Installation

[Documentation](README.md) · [Configuration](CONFIGURATION.md) · [Compatibility](COMPATIBILITY.md)

Use Minecraft **1.21.1**, **Java 21**, and matching loader builds of DifficultyEx and its dependencies. Install on the server and clients. This is a development port; use a backed-up test world for evaluation.

## Dependencies

These are the dependency versions pinned by this branch, not a promise that every newer version is compatible.

| Dependency | Version | Fabric | NeoForge |
| --- | --- | --- | --- |
| [PlayerEx](https://modrinth.com/mod/4UKlJSdk) | 5.0.1 | Required | Required |
| [Data Attributes](https://modrinth.com/mod/KCGxOJsE) | 3.0.0 | Required | Required |
| [Fzzy Config](https://modrinth.com/mod/VSNURh3q) | 0.7.7 for the loader | Required | Required |
| [Remnant](https://modrinth.com/mod/oLPaySSb) | 3.0.0 | Bundled in pinned PlayerEx | Bundled in pinned PlayerEx |
| [Fabric API](https://modrinth.com/mod/P7dR8mSH) | 0.116.15+1.21.1 | Required | Not used |
| [Fabric Language Kotlin](https://modrinth.com/mod/Ha28R6CL) | 1.13.7+kotlin.2.2.21 | Required | Not used |
| [Kotlin for Forge](https://modrinth.com/mod/kotlin-for-forge) | 5.11.0 complete distribution | Not used | Required |

Remnant must be present at runtime; the pinned PlayerEx download supplies it as a nested JAR. Development dependencies also declare it explicitly. Crunch is bundled. owo-lib is no longer required by DifficultyEx.

The current Fabric build requires Loader **0.20 or newer** and was development-tested with **0.20.1**. The earlier packaged checks used Loader 0.19.5 before this minimum changed. NeoForge packaged checks used 21.1.256; development remains pinned separately. See [development notes](../DEVELOPMENT.md) and [Fabric runtime acceptance](FABRIC_DEV_QA_2026-10-25.md).

## Set up

1. Stop the game or server and back up your world and configuration.
2. Put one DifficultyEx JAR for your loader in `mods`. Remove older DifficultyEx JARs.
3. Install the matching dependencies above. Use the complete Kotlin for Forge distribution for NeoForge.
4. Start once to generate `config/difficultyex/config.json5`.
5. Configure the server and run `/difficultyex validate` as an operator.

[Modrinth downloads](https://modrinth.com/mod/qdJ4GLvL/versions) · [CurseForge files](https://www.curseforge.com/minecraft/mc-mods/difficultyex/files/all)

Older files on these pages target 1.20.1. Check the actual version and loader before downloading.
