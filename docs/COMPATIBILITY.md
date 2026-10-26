# Compatibility and limitations

[Documentation](README.md) · [Installation](INSTALLATION.md) · [Port status](PORT_STATUS.md)

## Current target

Minecraft 1.21.1, Java 21, Fabric and NeoForge. The port targets PlayerEx 5.0.1, Data Attributes 3.0.0, Remnant 3.0.0 and Fzzy Config 0.7.7. Install corresponding loader builds on clients and servers. Quilt and legacy Forge are not acceptance targets for this branch.

## Optional integrations

GeckoLib-specific nameplate renderers and Traveler's Titles zone labels from the old release are not ported. Old configurable nameplate colors, scale and Y offset are also not currently supplied. Do not assume feature parity with the 1.20.1 download page.

## Verified and pending

| Area | Current evidence |
| --- | --- |
| Both packaged dedicated servers | Startup, operator commands and saved health/level persistence passed |
| Config migration | Synthetic and tested production fields passed; original files preserved |
| NeoForge nameplates | Singleplayer level, HP, bar and live enabled toggle passed |
| Config preferences | Serialization checks passed; authenticated two-client independence pending |
| Combat and XP | Mob combat and fake-player-attributed rewards passed; human equipment/enchantments and pickup pending |
| Structures | Loaded-origin, geometry and several generated variants passed; real movement and populated-world performance pending |
| Fabric development launch | Resolved with Loader 0.20.1; normal named server startup and expanded runtime checks passed |

The clean suite passes 63 tests. [Fabric runtime acceptance](FABRIC_DEV_QA_2026-10-25.md) also covers real combat, commands, legacy saves and a short 300-mob active-world sample. Full [QA results](QA_CHECKLIST.md) and [port status](PORT_STATUS.md) track the remaining work.

## Report a problem

Include Minecraft version, loader/version, DifficultyEx version, dependency versions, reproduction steps and the relevant log in a [GitHub issue](https://github.com/BareMinimumStudios/difficultyex/issues). Remove account tokens, addresses and other private information before attaching logs.
